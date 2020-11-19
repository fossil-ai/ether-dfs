package ether;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import links.ClientMasterJumpLink;
import links.ClientMasterLink;
import links.ClientMinionLink;

public class Client {
	
	ClientMasterJumpLink jumpLink;
	ClientMasterLink masterLink;
	ClientMinionLink minionLink;
	static Registry registry;
	private int clientID;
	

	public Client(String hostname, int port, String masterServerJumpLinkName){
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			jumpLink =  (ClientMasterJumpLink) registry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub.");
			clientID = jumpLink.clientJumpStart();
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Master Server Broken");
			e.printStackTrace();
		}
		
		
		this.setClientID(clientID);
		this.assignMinion(this.getClientID());
//		(ClientMinionLink) registry.lookup(masterServerLinkName);
		
	}
	
	public void createFile(String name) {
		try {
			masterLink.createFile(name);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFile(String name) {
		try {
			minionLink.readFile(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String assignMinion(int clientID) {
		try {
			masterLink.assignMinionToClient(clientID);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
	
	

}
