package ether;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import links.ClientMasterJumpLink;
import links.MinionMasterJumpLink;
import utils.ConfigReader;

public class MasterService {

	public static void main(String[] args) {

		ConfigReader reader = new ConfigReader();
		int REG_PORT = reader.getRegistryPort();
		String REG_HOST = reader.getRegistryHost();

		try {
			System.out.println("Creating Java RMI registry...");
			LocateRegistry.createRegistry(REG_PORT);
			System.out.println("Registry instance exported on port: " + REG_PORT);

			try {
				System.out.println("Launching MasterServer");
				Master masterServer = new Master();
				// Bind the remote object's stub in the registry
				Registry registry = LocateRegistry.getRegistry(REG_PORT);

				ClientMasterJumpLink clientMasterJLStub = (ClientMasterJumpLink) UnicastRemoteObject
						.toStub(masterServer);
				registry.rebind(reader.getRegistryClientJumpName(), clientMasterJLStub);

				MinionMasterJumpLink minionMasterJLStub = (MinionMasterJumpLink) UnicastRemoteObject
						.toStub(masterServer);
				registry.rebind(reader.getRegistryMinionJumpName(), minionMasterJLStub);

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
