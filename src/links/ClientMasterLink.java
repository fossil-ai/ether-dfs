package links;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.MinionLocation;

/**
 * @author mohamf1
 *
 * Methods the client can invoke on the master server.
 *
 */

public interface ClientMasterLink extends Remote {
	
	void createFile(String filename) throws AccessException, RemoteException, NotBoundException;
	
	public String assignMinionToClient(int clientID) throws IOException;

	MinionLocation locatePrimaryMinion(String fileName) throws RemoteException;

	int getClientCount() throws RemoteException;

}
