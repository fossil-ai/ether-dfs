package links;

import java.io.IOException;
import java.rmi.Remote;

/**
 * @author mohamf1
 *
 * Methods the master server invokes on any individual replica server.
 *
 */
public interface MasterMinionLink extends Remote {
	
	void createFile(String filename) throws IOException;

}
