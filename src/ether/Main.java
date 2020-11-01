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
import links.MasterMinionLink;
import utils.MinionLocation;

public class Main {
	
	public final static int REG_PORT = 50904;
	public final static String REG_ADDR = "localhost";
	public final static String MasterserverName = "MasterServer";

	static Registry registry;

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Welcome to EtherDFS");
		
		try {
			LocateRegistry.createRegistry(REG_PORT);
			registry = LocateRegistry.getRegistry(REG_PORT);
			// spawn master server here
			
			System.out.println("Spawning a minion server.");
			// TODO make file names global
			BufferedReader reader = new BufferedReader(new FileReader("filesys.conf"));
			int N = Integer.parseInt(reader.readLine().trim());
			int blocksize = Integer.parseInt(reader.readLine().trim());
			int replicationFactor = Integer.parseInt(reader.readLine().trim());
			
			Master masterServer = new Master(replicationFactor, blocksize);
			ClientMasterLink cm_stub = (ClientMasterLink) UnicastRemoteObject.toStub(masterServer);
			registry.rebind("ClientMasterLink", cm_stub);
			System.err.println("Server ready");
			
			MinionLocation location = new MinionLocation(0, "127.0.0.1", true);
			Minion minion = new Minion(0, "./"); 
		    MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(minion);
			registry.rebind("MinionFS" + 0, mm_stub);
			
			masterServer.addMinionInterface(location, mm_stub);


			reader.close();
			
			Client client = new Client();
			client.createFile("test.txt");

			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
