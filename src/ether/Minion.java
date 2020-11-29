package ether;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Files;

import links.MasterMinionLink;
import links.MinionMasterJumpLink;
import links.MinionMasterLink;
import links.MinionMinionLink;
import utils.ConfigReader;
import utils.FileContent;
import utils.FileNode;
import utils.LocalNameSpaceManager;
import utils.MinionLocation;
import links.ClientMinionLink;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, ClientMinionLink {

	private static int myPort = 50000;
	private static Socket socket;
	private static String IpAddress = "172.31.33.125"; // need to configure later. should be the main server address.

	public int minionID;
	public String directory;
	private MinionLocation location;
	private Registry masterRegistry;
	private Registry minionRegistry;
	private MinionMasterJumpLink jumpLink;
	private String minionMasterStubName;
	private MinionMasterLink masterLink;
	private MinionMinionLink minionMinionLink;
	private MinionMinionLink minionMinionLink2;
	private LocalNameSpaceManager nsManager;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;

	public Minion(String ip, String dir) throws RemoteException {

		System.out.println("Running minion server node on: " + ip);

		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		System.out.println("Registry address is " + REG_ADDR);
		int REG_PORT = reader.getRegistryPort();
		System.out.println("Registry port is " + REG_PORT);
		String masterServerJumpLinkName = reader.getRegistryMinionJumpName();

		try {
			masterRegistry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
			System.out.println("locate registry success");
			jumpLink = (MinionMasterJumpLink) masterRegistry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub for minion.");
			this.minionMasterStubName = jumpLink.minionJumpStart(masterRegistry);
			this.minionID = Integer.parseInt(minionMasterStubName.split("_")[1]);
			System.out.println("Your master-stub access name is: " + this.minionMasterStubName);
			System.out.println("Your assigned ID is: " + this.minionID);
			masterLink = (MinionMasterLink) masterRegistry.lookup(this.minionMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");
			
			
			System.out.println("Creating Java RMI registry for minion as well");
			LocateRegistry.createRegistry(REG_PORT + 1 + this.minionID);
			System.out.println("Registry instance exported on port: " + (REG_PORT + 1 + this.minionID));
			//minionRegistry = LocateRegistry.getRegistry(REG_ADDR, (REG_PORT + 1 + this.minionID));
			minionRegistry = LocateRegistry.getRegistry(ip, (REG_PORT + 1 + this.minionID));
			System.out.println("minion registry get");

			MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(this);
			minionRegistry.rebind("MasterMinionLink_" + this.minionID, mm_stub);
			System.out.println("the MasterMinion Link is:  " + "MasterMinionLink_" + this.minionID);

			ClientMinionLink cm_stub = (ClientMinionLink) UnicastRemoteObject.toStub(this);
			minionRegistry.rebind("ClientMinionLink_" + this.minionID, cm_stub);
			System.out.println("the ClientMinion Link is:  " + "ClientMinionLink_" + this.minionID);
			

			minionRegistry.rebind("MinionMinionLink", UnicastRemoteObject.toStub(this));
			System.out.println("the MinionMinion Link is:  " + "MinionMinionLink");
			
			

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
		locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();

		/*
		 * try { socket = new Socket(IpAddress, myPort); } catch (UnknownHostException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * Timer timer = new Timer(); timer.schedule(new TimerTask() {
		 * 
		 * @Override public void run() { try { heartBeat(); } catch (IOException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } ; } }, 0, 1000);
		 */

	}

//	@Override
//	public void createFile(String fileName) throws IOException {
//		File file = new File(this.directory + fileName);
//		locks.putIfAbsent(fileName, new ReentrantReadWriteLock());
//		ReentrantReadWriteLock lock = locks.get(fileName);
//		lock.writeLock().lock();
//		file.createNewFile();
//		lock.writeLock().unlock();
//	}

	public MinionLocation getLocation() {
		return this.location;
	}

	public void heartBeat() throws IOException {
		DataOutputStream heartBeat = new DataOutputStream(socket.getOutputStream());
		heartBeat.writeDouble(getMemSpace());
	}

	// return memory space used in percentage.
	public double getMemSpace() {
		File file = new File("/");
		return (int)(file.getFreeSpace() / file.getTotalSpace());
	}
	@Override
	public void createDir(String dirName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + dirName;
		File file = new File(newDirPath);
		if (!file.exists()) {
			file.mkdir();
		}
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
	}

	@Override
	public File readFile(String fileName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + fileName;
		File file = new File(newDirPath);
		return file;
	}

	@Override
	public void deleteFile(String fileName, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + fileName;
		File file = new File(newDirPath);
		locks.putIfAbsent(newDirPath, new ReentrantReadWriteLock());
		ReentrantReadWriteLock lock = locks.get(newDirPath);
		lock.writeLock().lock();
		file.delete();
		lock.writeLock().unlock();
		locks.remove(newDirPath);
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
	}

	@Override
	public File writeFile(FileContent content, FileNode cwd) throws RemoteException {
		if (getMemSpace() < 0.2)
		{
			System.out.println("current minion capacity is "  + getMemSpace());
			System.out.println("current minion has reached capacity, move to next minion");
			ConfigReader reader = new ConfigReader();
			minionRegistry = LocateRegistry.getRegistry(reader.getMinion3Addr(), (50903 + 1 + this.minionID));
			System.out.println( (50903 + 1 + this.minionID));
			System.out.println("registry get");
			try {
				minionMinionLink = (MinionMinionLink) minionRegistry.lookup("MinionMinionLink");
				System.out.println("registry lookup after");
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			minionMinionLink.writeFile( content,  cwd);
			if (minionMinionLink.getMemSpace() < 0.2 )
			{
				MinionMinionLink mtom1_stub = (MinionMinionLink) UnicastRemoteObject.toStub(this);
				System.out.println("rebind next");
				minionRegistry.rebind("MinionMinionLink_" + this.minionID, mtom1_stub);
				System.out.println("the MinionMinion Link is:  " + "MinionMinionLink_" + this.minionID);
				try {
					minionMinionLink2 = (MinionMinionLink) minionRegistry.lookup("MinionMinionLink_" + this.minionID);
				} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("current minion capacity is "  + minionMinionLink.getMemSpace());
				System.out.println("current minion has reached capacity, move to next minion");
				minionMinionLink2.writeFile( content,  cwd);
			}
		}
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + content.getName();
		try {
		    content.writeByte(newDirPath);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		return null;
	}

	@Override
	public void addClientToMinion(int id, ClientMinionLink link) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
