package links;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.FileNode;
import utils.MinionLocation;

/**
 * @author mohamf1
 *
 *         Methods the client can invoke on the master server.
 *
 */

public interface ClientMasterLink extends Remote {

	int getClientCount() throws RemoteException;

	ArrayList<String> listFilesAtCWD(FileNode cwdNode) throws RemoteException;

	FileNode getRootNode() throws RemoteException;

	public String[] getRandomMinionInfo() throws RemoteException;
	
	int assignClientID() throws RemoteException;

	List<MinionLocation> getMinionLocations() throws RemoteException;
	
	TreeMap<String, Integer> getMemoryDistribution() throws RemoteException;
	
	ArrayList<Integer> getAllMinionOwners(String fileName, FileNode cwd) throws RemoteException;
	
	boolean doesFileExist(String path) throws RemoteException;

}
