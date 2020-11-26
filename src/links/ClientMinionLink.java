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
	
	void createDir(String dirName) throws RemoteException;
	
	
//	void readFile(String filename) throws RemoteException;

}
