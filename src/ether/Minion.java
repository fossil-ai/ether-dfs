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
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.File;
import java.io.IOException; 
import java.nio.file.FileStore; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 

import links.MasterMinionLink;
import links.MinionMinionLink;
import utils.MinionLocation;
import links.ClientMinionLink;

public class Minion extends UnicastRemoteObject implements MasterMinionLink, MinionMinionLink, ClientMinionLink {
	
	public final static int REG_PORT = 50900;
	public final static String REG_ADDR = "localhost";
	
	public int id;
	public String directory;
	private Registry registry;
	
	private Map<Long, String> activeTxn; // map between active transactions and file names
	private Map<Long, Map<Long, byte[]>> txnFileMap; // map between transaction ID and corresponding file chunks
	private Map<String,	 List<MinionMinionLink> > filesReplicaMap; //replicas where files that this replica is its master are replicated  
	private Map<Integer, MinionLocation> minionServersLoc; // Map<ReplicaID, replicaLoc>
	private Map<Integer, MinionMinionLink> minionToMinionStubs; // Map<ReplicaID, replicaStub>
	private ConcurrentMap<String, ReentrantReadWriteLock> locks; // locks objects of the open files
	
	public Minion(int id, String dir) throws RemoteException {
		
		this.id = id;
		this.directory = "/tmp/minion_" + id + "/";
//		txnFileMap = new TreeMap<Long, Map<Long, byte[]>>();
//		activeTxn = new TreeMap<Long, String>();
//		filesReplicaMap = new TreeMap<String, List<ReplicaReplicaInterface>>();
//		replicaServersLoc = new TreeMap<Integer, ReplicaLoc>();
//		replicaServersStubs = new TreeMap<Integer, ReplicaReplicaInterface>();
//		locks = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
		
		File file = new File(this.directory);
		if (!file.exists()){
			file.mkdir();
		}
		
		try  {
			registry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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

	

}
