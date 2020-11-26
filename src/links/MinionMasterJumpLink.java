package links;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface MinionMasterJumpLink extends Remote{
	
	/*
	 * Allow all minions to have access to this stub - only use to notify master that a minion
	 *  wants to connected and to have an ID assigned.
	 * */
	
	public String minionJumpStart(Registry registry) throws RemoteException;


	public void registryBind (Registry registry , String name, MasterMinionLink link) throws RemoteException;


}
