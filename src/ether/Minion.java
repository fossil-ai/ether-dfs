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
import links.MinionMinionLink;
import utils.MinionLocation;
import links.ClientMinionLink;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, MinionMinionLink, ClientMinionLink {
	
	public final static int REG_PORT = 50904;
	public final static String REG_ADDR = "localhost";
	
	private static int myPort = 50000;
	private static Socket socket;
	
	public int id;
	public String directory;
	private MinionLocation location;
	private Registry registry;
	
	private Map<String,	 List<MinionMinionLink> > filesReplicaMap;
	private Map<Integer, MinionLocation> minionServersLoc;
	private Map<Integer, MinionMinionLink> minionToMinionStubs;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;
	private Map<Integer, ClientMinionLink> clientsConnectedMap;
	
	public Minion(String ip, String dir) throws RemoteException {
		
		try  {
			registry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		this.id = 0;
		this.directory = "/tmp/minion_" + id + "/";
		
		filesReplicaMap = new TreeMap<String, List<MinionMinionLink>>();
		minionServersLoc = new TreeMap<Integer, MinionLocation>();
		minionToMinionStubs = new TreeMap<Integer, MinionMinionLink>();
		clientsConnectedMap = new TreeMap<Integer, ClientMinionLink>();
		locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
		location = new MinionLocation(this.id, ip, true);
		
		File file = new File(this.directory);
		if (!file.exists()){
			file.mkdir();
		}
		
		try {
			socket = new Socket(ip, myPort);
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

	@Override
	public void takeCharge(String filename, List<MinionLocation> replicasResponsible) throws AccessException, RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		
		System.out.println("[@Replica] taking charge of file: "+ filename);
		System.out.println(replicasResponsible);
		
		List<MinionMinionLink> minionStubs = new ArrayList<MinionMinionLink>(replicasResponsible.size());
		
		for (MinionLocation loc : replicasResponsible) {
			// if the current locations is this replica .. ignore
			if (loc.getId() == this.id)
				continue;
			  
			// if this is a new replica generate stub for this replica
			if (!minionServersLoc.containsKey(loc.getId())){
				minionServersLoc.put(loc.getId(), loc);
				MinionMinionLink stub = (MinionMinionLink) registry.lookup("ReplicaClient"+loc.getId());
				minionToMinionStubs.put(loc.getId(), stub);
			}
			MinionMinionLink minionStub = minionToMinionStubs.get(loc.getId());
			minionStubs.add(minionStub);
		}
		
		filesReplicaMap.put(filename, minionStubs);
		
	}
	
	public MinionLocation getLocation() {
		return this.location;
	}
	
	public void heartBeat() throws IOException{
		DataOutputStream heartBeat = new DataOutputStream(socket.getOutputStream()); 
		heartBeat.writeDouble(getMemSpace());
	}
	// return free memory space in percentage.
	public double getMemSpace(){
		File file = new File("/dev/xvda1");
		return (double)(file.getFreeSpace()/(1024*1024))/(file.getTotalSpace()/(1024*1024));
	}

	@Override
	public void readFile(String filename) {
		// TODO Auto-generated method stub
		 try {
		      File file = new File(this.directory + filename);
		      locks.putIfAbsent(filename, new ReentrantReadWriteLock());
		      ReentrantReadWriteLock lock = locks.get(filename);
		      lock.readLock().lock();
		      Scanner scanner = new Scanner(file);
		      while (scanner.hasNextLine()) {
		        String data = scanner.nextLine();
		        System.out.println(data);
		      }
		      scanner.close();
		      lock.readLock().unlock();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }		 
	}

	@Override
	public void addClientToMinion(int id, ClientMinionLink link) {
		clientsConnectedMap.put(id, link);
	}

}
