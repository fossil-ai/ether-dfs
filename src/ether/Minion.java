package ether;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import links.MasterMinionLink;
import links.MinionMasterLink;
import links.MinionMinionLink;
import utils.ConfigReader;
import utils.FileContent;
import utils.FileNode;
import utils.LocalNameSpaceManager;
import utils.MinionLocation;
import utils.MinionManager;
import links.ClientMinionLink;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, ClientMinionLink, MinionMinionLink {

	private static int myPort = 50000;
	private static Socket socket;
	private static String IpAddress = "172.31.33.125"; // need to configure later. should be the main server address.

	public int minionID;
	public String directory;
	private MinionLocation location;
	private long memoryUsed;

	ConfigReader reader;
	private Registry masterRegistry;
	private Registry minionRegistry;

	private String minionMasterStubName;
	private MinionMasterLink masterLink;
	private LocalNameSpaceManager nsManager;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;
	private MinionManager minionManager;

	public Minion(String hostname, String port) throws RemoteException {

		System.out.println("Running minion server node on: " + hostname);

		this.reader = new ConfigReader();
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

			MinionMinionLink mtom_stub = (MinionMinionLink) UnicastRemoteObject.toStub(this);
			minionRegistry.rebind("MinionMinionLink_" + this.minionID, mtom_stub);
			System.out.println("the MinionMinion Link is:  " + "MinionMinionLink_" + this.minionID);

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

		this.masterLink.updateMemory(Integer.toString(this.minionID), this.getSizeOfDir());

		this.nsManager = new LocalNameSpaceManager(this.directory, Integer.toString(this.minionID));
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		this.locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
		this.minionManager = new MinionManager();

	}

	private void connectToMaster(String masterRegAddr, int masterRegPort) {
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
	public ArrayList<String> rerouteReadFile(String fileName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		ArrayList<String> lines = new ArrayList<String>();
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + fileName;

		File file = new File(newDirPath);
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				lines.add(data);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lines;
	}

	@Override
	public ArrayList<String> readFile(String fileName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		ArrayList<String> lines = new ArrayList<String>();
		String append_path = path[path.length - 1];
		String newDirPath = this.directory + append_path + "/" + fileName;
		
		if(this.nsManager.hasFile(newDirPath)) {
			File file = new File(newDirPath);
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String data = scanner.nextLine();
					lines.add(data);
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String newMinionID = Integer.toString(this.masterLink.getFileMinionOwner(Integer.toString(this.minionID), newDirPath));
			String minionMinionLink = "MinionMinionLink_" + newMinionID;
			Registry minionRegistry = LocateRegistry.getRegistry(this.minionManager.getMinionHost(newMinionID), this.minionManager.getMinionPort(newMinionID));
			try {
				MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
				lines = mmstub.rerouteReadFile(fileName, cwd);
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return lines;
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
		this.masterLink.updateMemory(Integer.toString(this.minionID), this.getSizeOfDir());
		return null;
	}

	public double sizeofDir() {
		Path folder = Paths.get(this.directory);
		double size = 0;
		try {
			size = Files.walk(folder).filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	@Override
	public double getSizeOfDir() {
		Path folder = Paths.get(this.directory);
		double size = 0;
		try {
			size = Files.walk(folder).filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	@Override
	public File createReplica(FileContent content, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
