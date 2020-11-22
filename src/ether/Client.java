package ether;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import links.ClientMasterJumpLink;
import links.ClientMasterLink;
import links.ClientMinionLink;
import utils.CommandParser;
import utils.FileNode;

public class Client {

	static Registry registry;
	ClientMasterJumpLink jumpLink;
	ClientMasterLink masterLink;
	ClientMinionLink minionLink;

	private int clientID;
	private String clientMasterStubName;
	private ArrayList<String> currentWorkingDirectory;
	private FileNode cwdNode;

	public enum ClientOperation {
		
		LS {
			@Override
			public void executeOp(String[] cmds, Client client) {
				ArrayList<String> files = client.masterLink.listFilesAtCWD(client.cwdNode);
				for (String filename : files){
					 System.out.println(filename); 
				} 
			}
		},
		
		NANO {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated method stub
				try {
					ProcessBuilder processBuilder = new ProcessBuilder(cmds[0], cmds[1]);
					processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
					Process p = processBuilder.start();
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					System.out.println("exception happened - here's what I know: ");
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		public abstract void executeOp(String[] cmds, Client client);
	}

	public Client(String hostname, int port, String masterServerJumpLinkName) {
		try {
			registry = LocateRegistry.getRegistry(hostname, port);
			jumpLink = (ClientMasterJumpLink) registry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub.");
			this.clientMasterStubName = jumpLink.clientJumpStart(registry);
			this.clientID = Integer.parseInt(clientMasterStubName.split("_")[1]);
			System.out.println("Your master-stub access name is: " + this.clientMasterStubName);
			System.out.println("Your assigned ID is: " + this.clientID);
			masterLink = (ClientMasterLink) registry.lookup(this.clientMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Master Server Broken");
			e.printStackTrace();
		}
		
		this.cwdNode = masterLink.getRootNode();
		this.currentWorkingDirectory = new ArrayList<String>();
	}

	public boolean execute(String[] cmds) {
		if (cmds[0].equals("exit")) {
			return true;
		} else {
			String op = CommandParser.parse(cmds);
			ClientOperation.valueOf(op).executeOp(cmds, this);
		}
		return false;
	}
	

	public void printCWD() {
		System.out.print("client" + this.clientID + "@ether-dfs:~/");
		for (int i = 1; i < this.currentWorkingDirectory.size(); i++) {
			System.out.print("/");
			System.out.print(this.currentWorkingDirectory.get(i));
		}
		System.out.print("$ ");
	}
	
	
	private void updateCWD(){
		this.currentWorkingDirectory.clear();
		for(int i = 0; i < this.cwdNode.children.size(); i++){
			
		}
	}

	private void createFile(String name) {
		try {
			masterLink.createFile(name);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readFile(String name) {
		try {
			minionLink.readFile(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
