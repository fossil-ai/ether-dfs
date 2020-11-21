package ether;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import links.MasterMinionLink;
import utils.ConfigReader;
import utils.MinionLocation;

public class MinionService {

	public static void main(String[] args) {

		try {
			System.out.println("Launching Minion at: " + InetAddress.getLocalHost().getHostAddress());
			String ip = InetAddress.getLocalHost().getHostAddress();
			Minion minion = new Minion(ip, "./");

		} catch (UnknownHostException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
