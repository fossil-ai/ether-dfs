package links;

import java.rmi.Remote;

/**
 * @author mohamf1
 *
 * Methods the client can invoke on the master server.
 *
 */

public interface ClientMasterLink extends Remote {
	
	void createFile(String filename);

}
