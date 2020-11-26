package links;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * @author mohamf1
 *
 * Methods the client/user can invoke on the replica server machines
 *
 */
public interface ClientMinionLink extends Remote {
	
	void createDir(String dirName) throws RemoteException;
	
	public void registryBind(Registry registry, String name, ClientMinionLink link);
	
//	void readFile(String filename) throws RemoteException;

}
