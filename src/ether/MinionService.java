package ether;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import links.MasterMinionLink;
import utils.MinionLocation;

public class MinionService {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		
//		MinionLocation location = new MinionLocation(0, "127.0.0.1", true);
//		Minion minion = new Minion(0, "./"); 
//	    MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(minion);
//		registry.rebind("MinionFS" + 0, mm_stub);
//		
//		masterServer.addMinionInterface(location, mm_stub);
		
		try {
			System.out.println("Launching Minion at: " + InetAddress.getLocalHost().getHostAddress());
			String ip = InetAddress.getLocalHost().getHostAddress();
			Minion minion = new Minion(ip, "./"); 
		    MasterMinionLink mm_stub = (MasterMinionLink) UnicastRemoteObject.toStub(minion);
			registry.rebind("MinionFS" + 0, mm_stub);
			
			masterServer.addMinionInterface(location, mm_stub);
			
		} catch (UnknownHostException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
