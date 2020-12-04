package ether;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import links.MasterMinionLink;
import links.MinionMasterLink;
import utils.ClientManager;
import utils.ConfigReader;
import utils.FileManager;
import utils.FileNode;
import utils.GlobalNameSpaceManager;
import utils.LoadBalancer;
import utils.LocalNameSpaceManager;
import utils.MinionLocation;
import utils.MinionManager;
import utils.NameSpaceSynchronizer;
import links.ClientMasterLink;
import links.ClientMinionLink;

public class Master extends UnicastRemoteObject implements MinionMasterLink, ClientMasterLink {

	public String name;
	public String address;
	private int client_count = 0;

	FileManager fileManager;
	MinionManager minionManager;
	ClientManager clientManager;
	GlobalNameSpaceManager globalNameSpaceManager;
	NameSpaceSynchronizer nameSpaceSynchronizer;
	LoadBalancer balancer;
	ConfigReader reader;

	Random random;

	public Master() throws RemoteException {
		this.fileManager = new FileManager();
		this.globalNameSpaceManager = new GlobalNameSpaceManager();
		this.nameSpaceSynchronizer = new NameSpaceSynchronizer(this.globalNameSpaceManager);
		this.minionManager = new MinionManager();
		this.clientManager = new ClientManager();
		this.balancer = new LoadBalancer();
		this.random = new Random();

		this.reader = new ConfigReader();
		int REG_PORT = this.reader.getRegistryPort();

		Registry registry = LocateRegistry.getRegistry(REG_PORT);

		registry.rebind("ClientMasterLink", (ClientMasterLink) UnicastRemoteObject.toStub(this));
		System.out.println("ClientMasterLink rebind success");

		registry.rebind("MinionMasterLink", (MinionMasterLink) UnicastRemoteObject.toStub(this));
		System.out.println("MinionMasterLink rebind success");
	}

	@Override
	public int getMinionCount() {
		return this.minionManager.minionsNum();
	}

	@Override
	public int getClientCount() {
		return this.clientManager.clientsNum();
	}

	@Override
	public int assignClientID() {
		/*
		 * Infinitely increment this - do not worry about looking up the old ID: If the
		 * client is turned off and logs back on - simply increment and give a new ID.
		 * This is okay because we will kill the cache anyways so nothing is preserved
		 * once a client session is dead.
		 */
		this.client_count = this.client_count + 1;
		return this.client_count;
	}

	@Override
	public String[] assignMinionInfo(String hostname, String port) {
		return this.minionManager.getMinionInfo(hostname, port);
	}

	@Override
	public ArrayList<String> listFilesAtCWD(FileNode cwdNode) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		for (Map.Entry<String, FileNode> entry : cwdNode.children.entrySet()) {
			listOfFiles.add(entry.getValue().filename);
		}
		return listOfFiles;
	}

	@Override
	public FileNode getRootNode() {
		return this.globalNameSpaceManager.getRoot();
	}

	@Override
	public void storeMinionLocation(MinionLocation location) throws RemoteException {
		// TODO Auto-generated method stub
		this.minionManager.addMinion(location);
	}

	@Override
	public void synchronize(String id, LocalNameSpaceManager nsManager) throws RemoteException {
		// TODO Auto-generated method stub
		this.nameSpaceSynchronizer.update(id, nsManager);
		this.nameSpaceSynchronizer.buildGlobalNameSpace();
		this.globalNameSpaceManager.rebuildGlobalPath();
	}

	@Override
	public String[] getRandomMinionInfo() throws RemoteException {
		String[] minionInfo = new String[3]; // Returns MinionID, HOST, PORT
		int randID = ThreadLocalRandom.current().nextInt(0, this.getMinionCount());
		MinionLocation location = this.minionManager.getMinionLocations().get(randID);
		minionInfo[0] = Integer.toString(location.getId());
		minionInfo[1] = location.getAddress();
		minionInfo[2] = Integer.toString(location.getPort());
		return minionInfo;
	}

	@Override
	public List<MinionLocation> getMinionLocations() throws RemoteException {
		return this.minionManager.getMinionLocations();
	}

	@Override
	public int updateMemory(String id, double size) {
		return this.balancer.updateMemoryStats(id, size);
	}

	@Override
	public TreeMap<String, Integer> getMemoryDistribution() throws RemoteException {
		TreeMap<String, Integer> memInfo = new TreeMap<String, Integer>();
		for (Entry<String, Integer> entry : this.balancer.getMemDist().entrySet()) {
			memInfo.put(entry.getKey(), entry.getValue());
		}
		return memInfo;
	}

	@Override
	public int getFileMinionOwner(String id, String dirPath) throws RemoteException {
		String minionRootDir = "/tmp/minion_" + id;
		String globalPath = dirPath.split(minionRootDir)[1];
		TreeMap<String, String> minionOwners = this.nameSpaceSynchronizer.getMinionOwners(globalPath);
		int randID = ThreadLocalRandom.current().nextInt(0, minionOwners.size());
		ArrayList<Integer> minionIDsWithFile = new ArrayList<Integer>();
		for (Entry<String, String> entry : minionOwners.entrySet()) {
			minionIDsWithFile.add(Integer.parseInt(entry.getKey()));
		}
		return minionIDsWithFile.get(randID);
	}

	@Override
	public int getUnderLoadedMinionID() throws RemoteException {
		return this.balancer.getNonOverloadedMinion();
	}

	@Override
	public ArrayList<Integer> getAllMinionOwners(String fileName, FileNode cwd) throws RemoteException {
		String[] path = cwd.path.split("tmp");
		String append_path = path[path.length - 1];
		String globalPath;
		if(path.length > 1) {
			globalPath = append_path + "/" + fileName;
		}
		else {
			globalPath = "/" + fileName;
		}
		System.out.println(globalPath);
		TreeMap<String, String> minionOwners = this.nameSpaceSynchronizer.getMinionOwners(globalPath);
		ArrayList<Integer> minionIDsWithFile = new ArrayList<Integer>();
		for (Entry<String, String> entry : minionOwners.entrySet()) {
			minionIDsWithFile.add(Integer.parseInt(entry.getKey()));
		}
		return minionIDsWithFile;
	}

	@Override
	public boolean doesFileExist(String path) throws RemoteException {
		return this.nameSpaceSynchronizer.fileExists(path);
	}

}
