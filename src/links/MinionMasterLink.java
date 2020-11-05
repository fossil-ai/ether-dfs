package links;

import java.rmi.Remote;

/**
 * @author mohamf1
 *
 * Methods the any individual replica server invokes on the master server.
 * 
 *  In other words: "What would you like the the master server to do or give you?"
 *
 */

public interface MinionMasterLink extends Remote{
	
	public int getMinionCount();
	
}
