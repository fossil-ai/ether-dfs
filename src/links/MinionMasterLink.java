package links;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.LocalNameSpaceManager;
import utils.MinionLocation;

/**
 * @author mohamf1
 *
 *         Methods the any individual replica server invokes on the master
 *         server.
 * 
 *         In other words: "What would you like the the master server to do or
 *         give you?"
 *
 */

public interface MinionMasterLink extends Remote {

	public int getMinionCount() throws RemoteException;

	public void storeMinionLocation(MinionLocation location) throws RemoteException;
	
	public void updateMemory(String id, double size) throws RemoteException;

	public void synchronize(String id, LocalNameSpaceManager nsManager) throws RemoteException;

	public String[] assignMinionInfo(String hostname, String port) throws RemoteException;

	public int getFileMinionOwner(String id, String newDirPath) throws RemoteException;
}
