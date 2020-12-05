package links;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.LocalNameSpaceManager;
import utils.MinionInfo;

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

	public void storeMinionInfo(MinionInfo location) throws RemoteException;

	public void synchronize(String id, LocalNameSpaceManager nsManager) throws RemoteException;
	
	public int updateMemory(String id, double size) throws RemoteException;

	public int getFileMinionOwner(String id, String newDirPath) throws RemoteException;

	public int getUnderLoadedMinionID() throws RemoteException;
	
	public int getMinionCount() throws RemoteException;
	
	public int getMyID(String code) throws RemoteException;
	
	public MinionInfo getMinionInfo(String id) throws RemoteException;

}
