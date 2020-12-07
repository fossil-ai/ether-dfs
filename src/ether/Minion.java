package ether;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import links.ClientMinionLink;
import links.MasterMinionLink;
import links.MinionMasterLink;
import links.MinionMinionLink;
import utils.ConfigReader;
import utils.FileContent;
import utils.FileNode;
import utils.LocalNameSpaceManager;
import utils.MinionInfo;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, ClientMinionLink, MinionMinionLink {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1569010521124125903L;
	public String hostname;
	public int port;
	public int minionID;
	public String directory;
	private MinionInfo info;
	private int loadStatus;

	ConfigReader reader;
	private Registry masterRegistry;
	private Registry minionRegistry;
	private String minionMasterStubName;
	private MinionMasterLink masterLink;

	private LocalNameSpaceManager nsManager;
	private ConcurrentMap<String, ReentrantReadWriteLock> locks;

	public Minion(String hostname, String port) throws RemoteException {

		System.out.println("Running minion server node on: " + hostname);
		this.port = Integer.parseInt(port);
		this.hostname = hostname;

		this.reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		System.out.println("Registry address is " + REG_ADDR);
		int REG_PORT = reader.getRegistryPort();
		System.out.println("Registry port is " + REG_PORT);

		try {

			this.connectToMaster(REG_ADDR, REG_PORT);

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

		this.directory = "/tmp/minion_" + this.minionID;
		System.out.println("Minion directory located on: " + this.directory);
		info = new MinionInfo(this.minionID, hostname, Integer.parseInt(port), this.directory, true);
		masterLink.storeMinionInfo(info);

		File file = new File(this.directory);
		if (!file.exists()) {
			file.mkdir();
		}

		this.nsManager = new LocalNameSpaceManager(this.directory, Integer.toString(this.minionID));
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
	}

	private void connectToMaster(String masterRegAddr, int masterRegPort) {
		try {
			masterRegistry = LocateRegistry.getRegistry(masterRegAddr, masterRegPort);
			this.minionMasterStubName = "MinionMasterLink";
			masterLink = (MinionMasterLink) masterRegistry.lookup(this.minionMasterStubName);
			this.minionID = this.masterLink.getMyID(this.hostname + this.port);
			System.out.println("Successfully fetched master-server link stub.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MinionInfo getInfo() {
		return this.info;
	}

	@Override
	public synchronized void createDir(String dirName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + dirName;
		File file = new File(newDirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.nsManager.buildTreeFromDir();
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
	}

	@Override
	public synchronized ArrayList<String> rerouteReadFile(String fileName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		ArrayList<String> lines = new ArrayList<String>();
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;

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
	public synchronized ArrayList<String> readFile(String fileName, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		String[] path = cwd.path.split("tmp");
		ArrayList<String> lines = new ArrayList<String>();
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;

		if (this.nsManager.hasFile(newDirPath)) {
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
			String newMinionID = Integer
					.toString(this.masterLink.getFileMinionOwner(Integer.toString(this.minionID), newDirPath));
			String minionMinionLink = "MinionMinionLink_" + newMinionID;
			Registry minionRegistry = LocateRegistry.getRegistry(
					this.masterLink.getMinionInfo(newMinionID).getAddress(),
					this.masterLink.getMinionInfo(newMinionID).getPort());
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
	public synchronized void rerouteDeleteFile(String fileName, FileNode cwd) throws RemoteException {
		System.out.println("REROUTE");
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;
		if (this.nsManager.hasFile(newDirPath)) {
			File file_ = new File(newDirPath);
			
			if(file_.isDirectory()) {
				this.deleteDirectory(file_);
			}
			else {
				file_.delete();
			}
						
			this.nsManager.buildTreeFromDir();
			this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
			this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
		}
	}
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

	@Override
	public synchronized void deleteFile(String fileName, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;


		
		if (this.nsManager.hasFile(newDirPath)) {
			File file_ = new File(newDirPath);
			
			if(file_.isDirectory()) {
				this.deleteDirectory(file_);
			}
			else {
				file_.delete();
			}
						
			this.nsManager.buildTreeFromDir();
			this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
			this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
		}

		String minionRootDir = "/tmp/minion_" + this.minionID;
		String globalPath = newDirPath.split(minionRootDir)[1];
		System.out.println(globalPath);

		ArrayList<Integer> ids = this.masterLink.getAllFileMinionOwners(globalPath);
		for (int i = 0; i < ids.size(); i++) {
			String newMinionID = Integer.toString(ids.get(i));
			if (!newMinionID.equalsIgnoreCase(Integer.toString(this.minionID))) {
				String minionMinionLink = "MinionMinionLink_" + newMinionID;
				Registry minionRegistry = LocateRegistry.getRegistry(
						this.masterLink.getMinionInfo(newMinionID).getAddress(),
						this.masterLink.getMinionInfo(newMinionID).getPort());
				try {
					MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
					System.out.println("DELETING HERE AS WELL");
					mmstub.rerouteDeleteFile(fileName, cwd);
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public synchronized void rerouteWriteFile(FileContent content, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("REPLICATING");
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + content.getName();
		System.out.println(newDirPath);
		try {
			content.writeByte(newDirPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.nsManager.buildTreeFromDir();
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
		System.out.println("DONE");
	}

	@Override
	public synchronized File writeFile(FileContent content, FileNode cwd) throws RemoteException {
		this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
		String underLoadedMinionID = null;
		if (this.loadStatus == -1 || this.loadStatus == 0 || this.masterLink.getMinionCount() < 2) {
			String[] path = cwd.path.split("tmp");
			String append_path = path[path.length - 1];
			append_path = pathCheck(append_path);
			String newDirPath = this.directory + append_path + content.getName();
			try {
				content.setPrimary(true);
				content.writeByte(newDirPath);
				// replicateFile(content,cwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.nsManager.buildTreeFromDir();
			this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
			this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
		} else {
			underLoadedMinionID = Integer.toString(this.masterLink.getUnderLoadedMinionID());
			System.out.println("High load status detected - re-routing file write to Minion " + underLoadedMinionID);
			String minionMinionLink = "MinionMinionLink_" + underLoadedMinionID;
			Registry minionRegistry = LocateRegistry.getRegistry(
					this.masterLink.getMinionInfo(underLoadedMinionID).getAddress(),
					this.masterLink.getMinionInfo(underLoadedMinionID).getPort());
			try {
				MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
				mmstub.rerouteWriteFile(content, cwd);
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (this.masterLink.getMinionCount() > 1) {
			String replicationMinionID = Integer.toString(this.minionID);
//			if (underLoadedMinionID == null) {
//				while (Integer.toString(this.minionID).equals(replicationMinionID)) {
//					replicationMinionID = Integer.toString(this.masterLink.getUnderLoadedMinionID());
//				}
//			} else {
				replicationMinionID = Integer.toString(
						this.masterLink.getReplicaMinionID(Integer.toString(this.minionID), underLoadedMinionID));
//			}
			System.out.println("REPLICATION - re-routing file write to Minion " + replicationMinionID);
			String minionMinionRepLink = "MinionMinionLink_" + replicationMinionID;
			Registry minionRepRegistry = LocateRegistry.getRegistry(
					this.masterLink.getMinionInfo(replicationMinionID).getAddress(),
					this.masterLink.getMinionInfo(replicationMinionID).getPort());
			try {
				MinionMinionLink mmRepStub = (MinionMinionLink) minionRepRegistry.lookup(minionMinionRepLink);
				mmRepStub.rerouteWriteFile(content, cwd);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}

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

	public String pathCheck(String path) {
		if (!path.substring(path.length() - 1, path.length()).equals("/"))
			path += "/";
		return path;
	}

	@Override
	public synchronized FileContent getFileContent(String fileName, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;
		FileContent content = null;
		System.out.println(newDirPath);
		if (this.nsManager.hasFile(newDirPath)) {
			content = new FileContent(newDirPath);
		} else {
			String newMinionID = Integer
					.toString(this.masterLink.getFileMinionOwner(Integer.toString(this.minionID), newDirPath));
			String minionMinionLink = "MinionMinionLink_" + newMinionID;
			Registry minionRegistry = LocateRegistry.getRegistry(
					this.masterLink.getMinionInfo(newMinionID).getAddress(),
					this.masterLink.getMinionInfo(newMinionID).getPort());
			try {
				MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
				content = mmstub.rerouteGetFileContent(fileName, cwd);
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return content;
	}

	@Override
	public synchronized FileContent rerouteGetFileContent(String fileName, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + fileName;
		FileContent content = new FileContent(newDirPath);
		return content;
	}

	@Override
	public boolean checkAlive() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void replicateFile(FileContent content, FileNode cwd) throws RemoteException {
		// TODO Auto-generated method stub
		FileContent replicaContent = content;
		String newMinionID = Integer.toString(this.masterLink.getUnderLoadedMinionID());
		String minionMinionLink = "MinionMinionLink_" + newMinionID;
		Registry minionRegistry = LocateRegistry.getRegistry(this.masterLink.getMinionInfo(newMinionID).getAddress(),
				this.masterLink.getMinionInfo(newMinionID).getPort());
		try {
			MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
			replicaContent.setPrimary(false);
			mmstub.rerouteWriteFile(replicaContent, cwd);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public File updateFile(FileContent content, FileNode cwd) throws RemoteException {
		
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + content.getName();

		if (this.nsManager.hasFile(newDirPath)) {
			try {
				content.writeByte(newDirPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String minionRootDir = "/tmp/minion_" + this.minionID;
		String globalPath = newDirPath.split(minionRootDir)[1];
		System.out.println(globalPath);

		ArrayList<Integer> ids = this.masterLink.getAllFileMinionOwners(globalPath);
		for (int i = 0; i < ids.size(); i++) {
			String newMinionID = Integer.toString(ids.get(i));
			if (!newMinionID.equalsIgnoreCase(Integer.toString(this.minionID))) {
				String minionMinionLink = "MinionMinionLink_" + newMinionID;
				Registry minionRegistry = LocateRegistry.getRegistry(
						this.masterLink.getMinionInfo(newMinionID).getAddress(),
						this.masterLink.getMinionInfo(newMinionID).getPort());
				try {
					MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
					mmstub.rerouteWriteFile(content, cwd);
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	@Override
	public String getTimeStamp(String filename, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + filename;
		if (this.nsManager.hasFile(newDirPath)) {
			Path pathname = Paths.get(newDirPath);
			FileTime time = null;
			try {
				time = Files.getLastModifiedTime(pathname);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return time.toString();
		} else {
			String newMinionID = Integer
					.toString(this.masterLink.getFileMinionOwner(Integer.toString(this.minionID), newDirPath));
			String minionMinionLink = "MinionMinionLink_" + newMinionID;
			Registry minionRegistry = LocateRegistry.getRegistry(
					this.masterLink.getMinionInfo(newMinionID).getAddress(),
					this.masterLink.getMinionInfo(newMinionID).getPort());
			try {
				MinionMinionLink mmstub = (MinionMinionLink) minionRegistry.lookup(minionMinionLink);
				String timestamp = mmstub.rerouteGetTimeStamp(filename, cwd);
				return timestamp;
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String rerouteGetTimeStamp(String filename, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		append_path = pathCheck(append_path);
		String newDirPath = this.directory + append_path + filename;
		Path pathname = Paths.get(newDirPath);
		FileTime time = null;
		try {
			time = Files.getLastModifiedTime(pathname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time.toString();
	}

	@Override
	public void resync() throws RemoteException {
		this.nsManager.buildTreeFromDir();
		this.masterLink.synchronize(Integer.toString(this.minionID), nsManager);
		this.loadStatus = this.masterLink.updateMemory(Integer.toString(this.minionID), this.sizeofDir());
	}

}
