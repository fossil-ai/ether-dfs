package ether;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import links.ClientMasterJumpLink;
import links.ClientMasterLink;
import links.ClientMinionLink;
import utils.CommandParser;
import utils.ConfigReader;
import utils.FileContent;
import utils.FileNode;

public class Client {

	Registry masterRegistry;
	Registry minionRegistry;
	ClientMasterJumpLink jumpLink;
	ClientMasterLink masterLink;
	ClientMinionLink minionLink;
	ClientMinionLink nextMinionLink;
	ClientMinionLink nextNextMinionLink;
	private int clientID;
	private String clientMasterStubName;
	private String clientMinionStubName;

	private ArrayList<String> currentWorkingDirectory;
	private FileNode cwdNode;
	private int depth = 0;

	public enum ClientOperation {

		RM {
			@Override
			public void executeOp(String[] cmds, Client client) {
				try {
					client.minionLink.deleteFile(cmds[1], client.cwdNode);
					client.updateFileNode();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		CAT {
			@Override
			public void executeOp(String[] cmds, Client client) {
				try {
					File file = client.minionLink.readFile(cmds[1], client.cwdNode);
					Scanner scanner = new Scanner(file);
					while (scanner.hasNextLine()) {
						String data = scanner.nextLine();
						System.out.println(data);
					}
					scanner.close();
				} catch (RemoteException | FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		LS {
			@Override
			public void executeOp(String[] cmds, Client client) {
				ArrayList<String> files;
				try {
					files = client.masterLink.listFilesAtCWD(client.cwdNode);
					for (String filename : files) {
						System.out.println(filename);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		CD {
			@Override
			public void executeOp(String[] cmds, Client client) {
				String path = client.cwdNode.path;
				if (cmds[1].equalsIgnoreCase("..")) {
					client.cwdNode = client.cwdNode.parent;
					client.depth -= 1;
				} else {
					path = path + "/" + cmds[1];
					client.cwdNode = client.cwdNode.children.get(path);
					client.depth += 1;
				}
				client.updateCWD();
			}
		},

		MKDIR {
			@Override
			public void executeOp(String[] cmds, Client client) {
				try {
					client.minionLink.createDir(cmds[1], client.cwdNode);
					client.updateFileNode();

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		NANO {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated method stub
				try {
					ProcessBuilder processBuilder = new ProcessBuilder(cmds[0], cmds[1]);
					processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
					Process p = processBuilder.start();
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					System.out.println("exception happened - here's what I know: ");
					e.printStackTrace();
					System.exit(-1);
				}

				try {
					FileContent content = new FileContent(cmds[1]);
					try {
					    if ( client.minionLink.getMemSpace()  < 0.2 ) {
							System.out.println("not enough space on this minion Server");
							System.out.println("moving to another minion Server");
							client.minionLink = client.nextMinionLink;
							client.nextMinionLink = client.nextNextMinionLink;
					    }
					}

					 catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						client.minionLink = client.nextMinionLink;
						client.nextMinionLink = client.nextNextMinionLink;
						System.out.println("catch !!!!!!!!");
					}
					try {
					    if ( client.minionLink.getMemSpace()  < 0.2 ) {
							System.out.println("not enough space on this minion Server");
							System.out.println("moving to another minion Server");
							client.minionLink = client.nextMinionLink;
					    }
					}

					 catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						client.minionLink = client.nextMinionLink;
					}
					
					client.minionLink.writeFile(content, client.cwdNode);
					client.updateFileNode();
					content.delete();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		public abstract void executeOp(String[] cmds, Client client);
	}

	public Client(String hostname, int port, String masterServerJumpLinkName) {
		try {
			masterRegistry = LocateRegistry.getRegistry(hostname, port);
			System.out.println("host name is " + hostname + "  port is " + port);
			jumpLink = (ClientMasterJumpLink) masterRegistry.lookup(masterServerJumpLinkName);
			System.out.println("Successfully fetched master-server jump-link stub.");
			this.clientMasterStubName = jumpLink.clientJumpStart(masterRegistry);
			this.clientID = Integer.parseInt(clientMasterStubName.split("_")[1]);
			System.out.println("Your master-stub access name is: " + this.clientMasterStubName);
			System.out.println("Your assigned ID is: " + this.clientID);
			masterLink = (ClientMasterLink) masterRegistry.lookup(this.clientMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");
			
			ConfigReader reader = new ConfigReader();
			String minion1_Addr = reader.getMinion1Addr();
			String minion2_Addr = reader.getMinion2Addr();
			String minion3_Addr = reader.getMinion3Addr();
			int minion1_Port = reader.getMinion1Port();
			int minion2_Port = reader.getMinion2Port();
			int minion3_Port = reader.getMinion3Port();
			

			//int minionID = masterLink.getRandomMinionID();
			//minionRegistry = LocateRegistry.getRegistry(minion1_Addr, port + minionID + 1 );
			minionRegistry = LocateRegistry.getRegistry(minion1_Addr, minion1_Port );
			System.out.println("address is " + minion1_Addr + "port is " +  minion1_Port);
			//this.clientMinionStubName = "ClientMinionLink_" + minionID;
			this.clientMinionStubName = "ClientMinionLink";
			System.out.println("ClientMinion Link is  :" + this.clientMinionStubName);
			minionLink = (ClientMinionLink) minionRegistry.lookup(this.clientMinionStubName);
			System.out.println("Successfully fetched minion link stub - client is connected to Minion " );
			
			minionRegistry = LocateRegistry.getRegistry(minion2_Addr, minion2_Port);
			System.out.println("minion 2 addr is " + minion2_Addr + "  minion 2 port is " +minion2_Port);
			this.clientMinionStubName = "ClientMinionLink"; 
			//this.clientMinionStubName = "ClientMinionLink_" + (minionID+1); 
			nextMinionLink = (ClientMinionLink) minionRegistry.lookup(this.clientMinionStubName);
			System.out.println("Successfully fetched minion link stub - client is connected to Minion " );
			

			minionRegistry = LocateRegistry.getRegistry(minion3_Addr, minion3_Port);
			System.out.println("minion 3 addr is " + minion3_Addr + "  minion 3 port is " + minion3_Port); 
			nextNextMinionLink = (ClientMinionLink) minionRegistry.lookup(this.clientMinionStubName);
			System.out.println("Successfully fetched minion link stub - client is connected to Minion " );
			

		
		} catch (RemoteException | NotBoundException e) {
			System.err.println("Master Server Broken");
			e.printStackTrace();
		}

		try {
			this.cwdNode = masterLink.getRootNode();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (depth == 0) {
			System.out.print("client" + this.clientID + "@ether-dfs:~");
		} else {
			System.out.print("client" + this.clientID + "@ether-dfs:");
			for (int i = 2; i < this.currentWorkingDirectory.size(); i++) {
				System.out.print("/");
				System.out.print(this.currentWorkingDirectory.get(i));
			}
		}
		System.out.print("$ ");
	}

	private void updateCWD() {
		this.currentWorkingDirectory.clear();
		String[] path_split = this.cwdNode.path.split("/");
		for (int i = 0; i < path_split.length; i++) {
			this.currentWorkingDirectory.add(path_split[i]);
		}
	}

	private void updateFileNode() {
		try {
			if (depth > 0) {
				String cwdPath = this.cwdNode.path;
				String[] cwdPaths = cwdPath.split("/");
				String search_path = cwdPaths[2];
				this.cwdNode = this.masterLink.getRootNode();
				for (int i = 1; i <= depth; i++) {
					this.cwdNode = this.cwdNode.children.get("/tmp/" + search_path);
					if (i != depth)
						search_path = search_path + "/" + cwdPaths[2 + i];
				}
			} else {
				this.cwdNode = this.masterLink.getRootNode();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
