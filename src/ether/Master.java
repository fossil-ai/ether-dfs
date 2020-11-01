package ether;

import java.io.IOException;
import java.rmi.*;  
import java.rmi.server.*;  
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import links.MasterMinionLink;
import links.MinionMasterLink;
import utils.MinionLocation;
import links.ClientMasterLink;

public class Master extends UnicastRemoteObject implements MinionMasterLink, ClientMasterLink {
	
	public int replicationFactor;
	public int blocksize;
	public String name;
	public String address;

	private Map<String,	List<MinionLocation> > fileMinionsMapping;
	private Map<String,	 MinionLocation> filePrimaryMinionMapping;
	private Map<Integer, String> activeTransactions; // active transactions <ID, fileName>
	private List<MinionLocation> minionLocations;
	private List<MasterMinionLink> minionMasterInvocation; 
	
	Random random;

	
	protected Master(int replicationFactor, int blocksize) throws RemoteException {
		
		this.replicationFactor = replicationFactor;
		this.blocksize = blocksize;
		
		// TODO Auto-generated constructor stub
		fileMinionsMapping = new HashMap<String, List<MinionLocation>>();
		filePrimaryMinionMapping = new HashMap<String, MinionLocation>();
		activeTransactions = new HashMap<Integer, String>();
		minionLocations = new ArrayList<MinionLocation>();
		minionMasterInvocation = new ArrayList<MasterMinionLink>();
		
		this.random = new Random();
	}
	

	public void addMinionInterface(MinionLocation minionLocation, MasterMinionLink stub){
		minionLocations.add(minionLocation);
		minionMasterInvocation.add((MasterMinionLink) stub);
	}


	@Override
	public void createFile(String filename) {
		// TODO Auto-generated method stub
		
		System.out.println("Master: File Created");
		int replicaIndices[] = new int[replicationFactor];
		List<MinionLocation> replicasResponsible = new ArrayList<MinionLocation>();

		Set<Integer> alreadySelectedIndices = new TreeSet<Integer>();

		for (int i = 0; i < replicaIndices.length; i++) {

			do {
				replicaIndices[i] = random.nextInt(replicationFactor);
				
			} while(alreadySelectedIndices.contains(replicaIndices[i]));


			alreadySelectedIndices.add(replicaIndices[i]);
			replicasResponsible.add(minionLocations.get(replicaIndices[i]));

			try {
				minionMasterInvocation.get(replicaIndices[i]).createFile(filename);
			} catch (IOException e) { 
				e.printStackTrace();
			}

		}

		// the primary replica is the first lucky replica picked
		int primaryReplicaIndex = replicaIndices[0];
		try {
			minionMasterInvocation.get(primaryReplicaIndex).takeCharge(filename, replicas);
		} catch (RemoteException | NotBoundException e) {
			// couldn't assign the master replica
			e.printStackTrace();
		}

		fileMinionsMapping.put(filename, replicasResponsible);
		filePrimaryMinionMapping.put(filename, minionLocations.get(primaryReplicaIndex));
		
	}
	
	private int[] getRandomReplicaIndices(){
		return null;
	}

	

}
