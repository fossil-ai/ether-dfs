package ether;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
	
	private static ServerSocket server;
	private static Socket socket;
	private static int port = 50000;
	
	Random random;
	
	protected Master() throws RemoteException {
		this.fileManager = new FileManager();
		this.minionManager = new MinionManager();
		this.random = new Random();
		
		try {
			server = new ServerSocket(port);
			socket = server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
	
	
	@Override
	public MinionLocation locatePrimaryMinion(String fileName) throws RemoteException {
		return fileManager.getPrimaryFileLocation(fileName);
	}
	
	public int assignMinionToClient(){
		System.out.println("Master: Assigning Client to Minion");
		this.minionManager.getMinionMasterInvocation().get(0).addClientToMinion(1234, null);;
		return 0;
	}


	@Override
	public int getMinionCount() {
		return this.minionManager.minionsNum();
	}

	public void listenHeartBeat() {
		
	}
	
}
