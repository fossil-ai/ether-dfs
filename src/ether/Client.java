package ether;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import links.ClientMasterLink;

public class Client {
	
	ClientMasterLink masterLink;
	static Registry registry;
	private int clientID;
	

	public Client(String hostname, int port, String masterServerLinkName){
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			masterLink =  (ClientMasterLink) registry.lookup(masterServerLinkName);
			System.out.println("Successfully fetched master server stub.");
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Master Server Broken");
			e.printStackTrace();
		}
		
		this.clientID = 1234; // Temporary ID
		
	}
	
	public void createFile(String name) {
		try {
			masterLink.createFile(name);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void assignMinion() {
		try {
			masterLink.assignMinionToClient();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
	
	

}
