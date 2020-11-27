package links;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import utils.FileNode;
import utils.MinionLocation;

/**
 * @author mohamf1
 *
 *         Methods the client can invoke on the master server.
 *
 */

public interface ClientMasterLink extends Remote {

	void createFile(String filename) throws AccessException, RemoteException, NotBoundException;

	int getClientCount() throws RemoteException;
	
	ArrayList<String> listFilesAtCWD(FileNode cwdNode) throws RemoteException;
	
	FileNode getRootNode() throws RemoteException;

	String getRandomMinionID() throws RemoteException;

}
