package links;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import utils.MinionLocation;

/**
 * @author mohamf1
 *
 * Methods the master server invokes on any individual replica server.
 *
 */
public interface MasterMinionLink extends Remote {
	
	void createFile(String filename) throws IOException;
	
	void takeCharge(String filename, List<MinionLocation> replicasResponsible) throws AccessException, RemoteException, NotBoundException;

	double getMemSpace();
}
