package links;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface ClientMasterJumpLink extends Remote {

	/*
	 * Allow all clients to have access to this stub - only use to notify client
	 * wants to connected and to have an ID assigned.
	 */

	public String clientJumpStart(Registry registry) throws RemoteException;;

}
