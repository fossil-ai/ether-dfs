package links;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.FileContent;
import utils.FileNode;

/**
 * @author mohamf1
 *
 * Methods the client/user can invoke on the replica server machines
 *
 */
public interface ClientMinionLink extends Remote {
	
	void createDir(String dirName, FileNode cwd) throws RemoteException;

	File readFile(String filename, FileNode cwd) throws RemoteException;
	
	void deleteFile(String filename, FileNode cwd) throws RemoteException;
	
	File writeFile(FileContent fileContent, FileNode cwd) throws RemoteException;

}
