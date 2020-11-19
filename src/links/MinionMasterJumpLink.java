package links;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MinionMasterJumpLink extends Remote{
	
	/*
	 * Allow all minions to have access to this stub - only use to notify master that a minion
	 *  wants to connected and to have an ID assigned.
	 * */
	
	public int minionJumpStart() throws RemoteException;

}
