package ether;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import links.MasterMinionLink;
import utils.MinionLocation;

public class MinionService {

	public static void main(String[] args) {
		
		Registry registry;
		final String REG_ADDR = "localhost";
		final int REG_PORT = 50904;
		
		try {
			registry = LocateRegistry.getRegistry(REG_ADDR, REG_PORT);
			System.out.println("Launching Minion at: " + InetAddress.getLocalHost().getHostAddress());
			String ip = InetAddress.getLocalHost().getHostAddress();
			Minion minion = new Minion(ip, "./"); 
		    MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(minion);
			registry.rebind("MinionFS" + 0, mm_stub);
			
			
		} catch (UnknownHostException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
