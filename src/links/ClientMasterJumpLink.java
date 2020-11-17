package links;

import java.rmi.Remote;

public interface ClientMasterJumpLink extends Remote{
	
	/*
	 * Allow all clients to have access to this stub - only use to notify client wants to connected and to have an ID assigned.
	 * */
	
	public int clientJumpStart();

}
