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
	private String clientMasterStubName;
	

	public Client(String hostname, int port, String masterServerJumpLinkName){
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			jumpLink =  (ClientMasterJumpLink) registry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub.");
			this.clientMasterStubName = jumpLink.clientJumpStart(registry);
			this.clientID = Integer.parseInt(clientMasterStubName.split("_")[1]);
			System.out.println("Your master-stub access name is: " + this.clientMasterStubName);
			System.out.println("Your assigned ID is: " + this.clientID);
			masterLink =  (ClientMasterLink) registry.lookup(this.clientMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Master Server Broken");
			e.printStackTrace();
		}
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
	
	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
	
	

}
