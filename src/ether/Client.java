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
	public final static int REG_PORT = 50904;
	public final static String REG_ADDR = "localhost";
	public final static String MasterServerName = "MasterServer";
	
	public Client(){
		try {
			registry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
			masterLink =  (ClientMasterLink) registry.lookup("ClientMasterLink");
			System.out.println("[@client] Master Stub fetched successfuly");
		} catch (RemoteException | NotBoundException e) {
			// fatal error .. no registry could be linked
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
