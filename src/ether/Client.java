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
	
	public Client(String hostname, int port, String masterServerLinkName){
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			masterLink =  (ClientMasterLink) registry.lookup(masterServerLinkName);
			System.out.println("Successfully fetched master server stub.");
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
	
	

}
