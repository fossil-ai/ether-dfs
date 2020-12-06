package links;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.FileNode;
import utils.Lease;
import utils.MinionInfo;

/**
 * @author mohamf1
 *
 *         Methods the client can invoke on the master server.
 *
 */

public interface ClientMasterLink extends Remote {

	ArrayList<String> listFilesAtCWD(FileNode cwdNode) throws RemoteException;

	ArrayList<Integer> getAllMinionOwners(String fileName, FileNode cwd) throws RemoteException;

	List<MinionInfo> getMinionInfoList() throws RemoteException;

	FileNode getRootNode() throws RemoteException;

	public String[] getRandomMinionInfo() throws RemoteException;

	int assignClientID() throws RemoteException;

	TreeMap<String, Integer> getMemoryDistribution() throws RemoteException;

	boolean doesFileExist(String path) throws RemoteException;

	Lease lease(String ClientID, String globalFilename) throws RemoteException;

}
