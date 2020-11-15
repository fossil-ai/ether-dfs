package ether;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;  
import java.rmi.server.*;  
import java.util.UUID;
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

	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 50000;
	Map <String , Double> MinionsList = new HashMap <String , Double>();
	
	Random random;
	
	protected Master() throws RemoteException {
		this.fileManager = new FileManager();
		this.minionManager = new MinionManager();
		this.random = new Random();
		
		
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("server socket started!!!!");
			while(true) {

				socket = serverSocket.accept();
				System.out.println("connection established!!!!");
				threadServer thread = new threadServer(socket);
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//include all the minions IP address; initialize minions memory space to 1;
		//MinionsList.put("172.31.33.125", 1);
		MinionsList.put("172.31.46.197", 1.00);
		
		
		
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
	
	class threadServer extends Thread{		
		Socket threadSocket;
		public threadServer ( Socket threadSocket){
			this.threadSocket = threadSocket;
		}
		
		public void run (){
			double percent;
			String tempAddress;
				try {
					DataInputStream input = new DataInputStream(threadSocket.getInputStream());
					tempAddress = threadSocket.getRemoteSocketAddress().toString();
					System.out.println("input is " + input + "address is "+ tempAddress);
					if (MinionsList.containsKey(tempAddress)) {
						System.out.println(" address is in address book, update percent value");
						MinionsList.put(tempAddress, input.readDouble());
						}
					else {
						System.out.println("minions is down, remove from the list");
						MinionsList.remove(tempAddress);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		}
	}
	
	
}
