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
	
	private String minionMasterStubName;
	private MinionMasterLink masterLink;
	private LocalNameSpaceManager nsManager;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;

	public Minion(String hostname, String port) throws RemoteException {

		System.out.println("Running minion server node on: " + hostname);

		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		System.out.println("Registry address is " + REG_ADDR);
		int REG_PORT = reader.getRegistryPort();
		System.out.println("Registry port is " + REG_PORT);

		try {
			
			this.connectToMaster(REG_ADDR, REG_PORT);
			
			String[] minionInfo = masterLink.assignMinionInfo(hostname, port);
			this.minionID = Integer.parseInt(minionInfo[0]);
			this.directory = minionInfo[1];
			
			System.out.println("Creating Java RMI registry for minion as well");
			LocateRegistry.createRegistry(Integer.parseInt(port));
			System.out.println("Registry instance exported on port: " + port);

			minionRegistry = LocateRegistry.getRegistry(hostname, Integer.parseInt(port));
			
			MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(this);
			minionRegistry.rebind("MasterMinionLink_" + this.minionID, mm_stub);
			System.out.println("the MasterMinion Link is:  " + "MasterMinionLink_" + this.minionID);

			ClientMinionLink cm_stub = (ClientMinionLink) UnicastRemoteObject.toStub(this);
			minionRegistry.rebind("ClientMinionLink_" + this.minionID, cm_stub);
			System.out.println("the ClientMinion Link is:  " + "ClientMinionLink_" + this.minionID);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		System.out.println("Minion directory located on: " + this.directory);
		location = new MinionLocation(this.minionID, hostname, Integer.parseInt(port), this.directory, true);
		masterLink.storeMinionLocation(location);

		File file = new File(this.directory);
		if (!file.exists()) {
			file.mkdir();
		}

		this.nsManager = new LocalNameSpaceManager(this.directory, Integer.toString(this.minionID));
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();

	}
	
	private void connectToMaster(String masterRegAddr, int masterRegPort){
		try {
			masterRegistry = LocateRegistry.getRegistry(masterRegAddr, masterRegPort);
			this.minionMasterStubName = "MinionMasterLink";
			masterLink = (MinionMasterLink) masterRegistry.lookup(this.minionMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public MinionLocation getLocation() {
		return this.location;
	}

	public void heartBeat() throws IOException {
		DataOutputStream heartBeat = new DataOutputStream(socket.getOutputStream());
		heartBeat.writeDouble(getMemSpace());
	}


	public double getMemSpace() {
		File file = new File("/");
		return ((double) file.getFreeSpace() / (double) file.getTotalSpace());

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
		this.nsManager.buildTreeFromDir();
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
		this.nsManager.buildTreeFromDir();
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
	}

	@Override
	public File writeFile(FileContent content, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + content.getName();
		try {
			content.writeByte(newDirPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.nsManager.buildTreeFromDir();
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		return null;
	}


}
