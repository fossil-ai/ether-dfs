package links;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import utils.FileNode;

/**
 * @author mohamf1
 *
 * Methods the client/user can invoke on the replica server machines
 *
 */
public interface ClientMinionLink extends Remote {
	
	void createDir(String dirName, FileNode cwd) throws RemoteException;

	File readFile(String dirName, FileNode cwd) throws RemoteException;

	public void registryBind(Registry registry, String name, ClientMinionLink link) throws RemoteException;
	

//	void readFile(String filename) throws RemoteException;

}
