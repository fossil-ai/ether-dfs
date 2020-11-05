package ether;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import links.ClientMasterLink;

public class MasterService {

	public static void main(String[] args) {
		
		final String REG_ADDR = "localhost";
		final int REG_PORT = 50904;
		final String MS_LINKNAME = "MasterLink";
		
		System.out.println("Creating Java RMI registry");
		LocateRegistry.createRegistry(REG_PORT);
		registry = LocateRegistry.getRegistry(REG_PORT);
		// spawn master server here
		
		System.out.println("L.");
		// TODO make file names global
		BufferedReader reader = new BufferedReader(new FileReader("filesys.conf"));
		int N = Integer.parseInt(reader.readLine().trim());
		int blocksize = Integer.parseInt(reader.readLine().trim());
		int replicationFactor = Integer.parseInt(reader.readLine().trim());
		
		Master masterServer = new Master(replicationFactor, blocksize);
		ClientMasterLink cm_stub = (ClientMasterLink) UnicastRemoteObject.toStub(masterServer);
		registry.rebind("ClientMasterLink", cm_stub);
		System.err.println("Server ready");

	}

}
