package ether;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


import links.ClientMasterLink;

public class MasterService {

	public static void main(String[] args) {
		
		final int REG_PORT = 50904;
		final String MS_LINKNAME = "MasterLink";
		
		Registry registry;
		
		System.out.println("Creating Java RMI registry...");
		
		try {
			
			LocateRegistry.createRegistry(REG_PORT);
			System.out.println("Registry instance exported on port: " + REG_PORT);
			registry = LocateRegistry.getRegistry(REG_PORT);
			
			System.out.println("Parsing server/minion configurations...");
			
			try {
				
				System.out.println("Launching MasterServer");
				Master masterServer = new Master();
				ClientMasterLink cm_stub = (ClientMasterLink) UnicastRemoteObject.toStub(masterServer);
				registry.rebind(MS_LINKNAME, cm_stub);
				System.err.println("Ready and running...");
				
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
