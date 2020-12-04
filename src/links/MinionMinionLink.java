package links;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import utils.FileContent;
import utils.FileNode;

/**
 * @author mohamf1
 *
 *         Methods any one replica server/minion can invoke on another.
 *
 */

public interface MinionMinionLink extends Remote {

	public File createReplica(FileContent content, FileNode cwd) throws RemoteException;
	
	public ArrayList<String> rerouteReadFile(String fileName, FileNode cwd) throws RemoteException;

	public void rerouteDeleteFile(String fileName, FileNode cwd) throws RemoteException;
	
	public void rerouteWriteFile(FileContent content, FileNode cwd) throws RemoteException;
	
	public FileContent rerouteGetFileContent(String filename, FileNode cwd) throws RemoteException;

}
