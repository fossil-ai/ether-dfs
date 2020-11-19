package links;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author mohamf1
 *
 * Methods the client/user can invoke on the replica server machines
 *
 */
public interface ClientMinionLink extends Remote {
	
	void readFile(String filename) throws RemoteException;

}
