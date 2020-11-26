package ether;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.*;

import links.MasterMinionLink;
import links.MinionMasterJumpLink;
import links.MinionMasterLink;
import links.MinionMinionLink;
import utils.ConfigReader;
import utils.GlobalNameSpaceManager;
import utils.LocalNameSpaceManager;
import utils.MinionLocation;
import utils.NameSpaceSynchronizer;
import links.ClientMasterJumpLink;
import links.ClientMasterLink;
import links.ClientMinionLink;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, MinionMinionLink, ClientMinionLink {

	private static int myPort = 50000;
	private static Socket socket;
	private static String IpAddress = "172.31.33.125"; // need to configure later. should be the main server address.

	public int minionID;
	public String directory;
	private MinionLocation location;
	private Registry registry;
	private MinionMasterJumpLink jumpLink;
	private String minionMasterStubName;
	private MinionMasterLink masterLink;

	private Map<String, List<MinionMinionLink>> filesReplicaMap;
	private Map<Integer, MinionLocation> minionServersLoc;
	private Map<Integer, MinionMinionLink> minionToMinionStubs;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;
	private Map<Integer, ClientMinionLink> clientsConnectedMap;
	
	private LocalNameSpaceManager nsManager;

	public Minion(String ip, String dir) throws RemoteException {
		
		filesReplicaMap = new TreeMap<String, List<MinionMinionLink>>();
		minionServersLoc = new TreeMap<Integer, MinionLocation>();
		minionToMinionStubs = new TreeMap<Integer, MinionMinionLink>();
		clientsConnectedMap = new TreeMap<Integer, ClientMinionLink>();
		locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();

		System.out.println("Running minion server node on: " + ip);
		
		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		System.out.println("Registry address is " + REG_ADDR);
		int REG_PORT = reader.getRegistryPort();
		System.out.println("Registry port is " + REG_PORT);
		String masterServerJumpLinkName = reader.getRegistryMinionJumpName();

		try {
			registry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
			System.out.println("locate registry success");
			jumpLink = (MinionMasterJumpLink) registry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub for minion.");
			this.minionMasterStubName = jumpLink.minionJumpStart(registry);
			this.minionID = Integer.parseInt(minionMasterStubName.split("_")[1]);
			System.out.println("Your master-stub access name is: " + this.minionMasterStubName);
			System.out.println("Your assigned ID is: " + this.minionID);
			masterLink = (MinionMasterLink) registry.lookup(this.minionMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");

			MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(this);
			registry.rebind("MasterMinionLink_" + this.minionID, mm_stub);
			
			ClientMinionLink cm_stub = (ClientMinionLink) UnicastRemoteObject.toStub(this);
			registry.rebind("ClientMinionLink_" + this.minionID, cm_stub);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		this.directory = "/tmp/" + "minion_" + this.minionID;
		System.out.println("Minion directory located on: " + this.directory);
		location = new MinionLocation(this.minionID, ip, this.directory, true, 0);
		masterLink.storeMinionLocation(location);

		File file = new File(this.directory);
		if (!file.exists()) {
			file.mkdir();
		}
		
		this.nsManager = new LocalNameSpaceManager(this.directory, Integer.toString(this.minionID));
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		

		/*
		try {
			socket = new Socket(IpAddress, myPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	        @Override
	        public void run() {
	        	try {
					heartBeat();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            ;
	        }
	    }, 0, 1000);
	    */

	}

	@Override
	public void createFile(String fileName) throws IOException {
		File file = new File(this.directory + fileName);
		locks.putIfAbsent(fileName, new ReentrantReadWriteLock());
		ReentrantReadWriteLock lock = locks.get(fileName);
		lock.writeLock().lock();
		file.createNewFile();
		lock.writeLock().unlock();
	}


	public MinionLocation getLocation() {
		return this.location;
	}

	public void heartBeat() throws IOException {
		DataOutputStream heartBeat = new DataOutputStream(socket.getOutputStream());
		heartBeat.writeDouble(getMemSpace());
	}

	// return free memory space in percentage.
	public double getMemSpace() {
		File file = new File("/dev/xvda1");
		return (double) (file.getFreeSpace() / (1024 * 1024)) / (file.getTotalSpace() / (1024 * 1024));
	}

//	@Override
//	public void readFile(String filename) {
//		// TODO Auto-generated method stub
//		try {
//			File file = new File(this.directory + filename);
//			locks.putIfAbsent(filename, new ReentrantReadWriteLock());
//			ReentrantReadWriteLock lock = locks.get(filename);
//			lock.readLock().lock();
//			Scanner scanner = new Scanner(file);
//			while (scanner.hasNextLine()) {
//				String data = scanner.nextLine();
//				System.out.println(data);
//			}
//			scanner.close();
//			lock.readLock().unlock();
//		} catch (FileNotFoundException e) {
//			System.out.println("An error occurred.");
//			e.printStackTrace();
//		}
//	}

	@Override
	public void addClientToMinion(int id, ClientMinionLink link) {
		clientsConnectedMap.put(id, link);
	}

	@Override
	public void createDir(String dirName) throws RemoteException {
		// TODO Auto-generated method stub
		File file = new File(this.directory + "/" + dirName);
		if (!file.exists()) {
			file.mkdir();
		}
	}


}
