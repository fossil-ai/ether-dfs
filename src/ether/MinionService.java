package ether;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class MinionService {

	public static void main(String[] args) {

		String hostname = args[0];
		String port = args[1];

		try {
			System.out.println("Launching Minion at: " + InetAddress.getLocalHost().getHostAddress());
			Minion minion = new Minion(hostname, port);

		} catch (UnknownHostException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
