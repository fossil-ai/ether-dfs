package ether;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import utils.ConfigReader;

public class MasterService {

	public static void main(String[] args) {

		ConfigReader reader = new ConfigReader();
		int REG_PORT = reader.getRegistryPort();

		try {
			System.out.println("Creating Java RMI registry...");
			LocateRegistry.createRegistry(REG_PORT);
			System.out.println("Registry instance exported on port: " + REG_PORT);

			try {
				System.out.println("Launching MasterServer");
				Master masterServer = new Master();

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
