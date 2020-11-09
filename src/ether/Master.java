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
import utils.FileManager;
import utils.MinionLocation;
import utils.MinionManager;
import links.ClientMasterLink;

public class Master extends UnicastRemoteObject implements MinionMasterLink, ClientMasterLink {
	
	public String name;
	public String address;
	
	FileManager fileManager;
	MinionManager minionManager;
	
	Random random;
	
	protected Master() throws RemoteException {
		this.fileManager = new FileManager();
		this.minionManager = new MinionManager();
		this.random = new Random();
	}
	
	public void addMinionInterface(MinionLocation minionLocation, MasterMinionLink stub){
		this.minionManager.addMinion(minionLocation, stub);
	}

	@Override
	public void createFile(String filename) throws AccessException, RemoteException, NotBoundException {
		System.out.println("Master: File Created");
		for (int i = 0; i < this.minionManager.minionsNum(); i++) {
			try {
				this.minionManager.getMinionMasterInvocation().get(i).createFile(filename);
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
		int primaryReplicaIndex = 0;
		this.minionManager.getMinionMasterInvocation().get(primaryReplicaIndex).takeCharge(filename, this.minionManager.getMinionLocations());
		this.fileManager.assignSelectedMinionsToFile(filename, this.minionManager.getMinionLocations());
		this.fileManager.assignPrimaryMinionToFile(filename, primaryReplicaIndex, this.minionManager.getMinionLocations());
		
	}
	
	private int[] getRandomReplicaIndices(){
		return null;
	}
	
	@Override
	public MinionLocation locatePrimaryMinion(String fileName) throws RemoteException {
		return fileManager.getPrimaryFileLocation(fileName);
	}


	@Override
	public int getMinionCount() {
		return this.minionManager.minionsNum();
	}


	

}
