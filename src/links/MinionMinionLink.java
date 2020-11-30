package links;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.FileContent;
import utils.FileNode;

/**
 * @author mohamf1
 *
 * Methods any one replica server/minion can invoke on another.
 *
 */

public interface MinionMinionLink extends Remote{
	
	public File writeFile(FileContent content, FileNode cwd) throws RemoteException ;
	
	public double getMemSpace() throws RemoteException ;


}
