package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import links.ClientMasterLink;
import links.MasterMinionLink;

public class ClientManager {
	
	private Map<Integer, ClientMasterLink> clientMasterInvocation; 
	
	public ClientManager(){
		this.clientMasterInvocation = new TreeMap<Integer, ClientMasterLink>();
	}
	
	public void addClient(int id, ClientMasterLink stub) {
		System.out.println("Adding client-master stub for client ID: " + id);
		this.clientMasterInvocation.put(id, stub);
	}
	
	public void removeClient(int id) {
		System.out.println("Removing client-master stub for client ID: " + id);
		this.clientMasterInvocation.remove(id);
	}
	
	public int clientsNum() {
		return this.clientMasterInvocation.size();
	}


}
