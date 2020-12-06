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
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import links.ClientMasterLink;
import links.ClientMinionLink;
import links.MinionMinionLink;
import utils.CommandParser;
import utils.ConfigReader;
import utils.FileContent;
import utils.FileNode;
import utils.Lease;
import utils.MinionInfo;

public class Client {

	Registry masterRegistry;
	Registry minionRegistry;
	ClientMasterLink masterLink;
	ClientMinionLink minionLink;
	private int clientID;
	private String clientMasterStubName;
	private String clientMinionStubName;

	private ArrayList<String> currentWorkingDirectory;
	private FileNode cwdNode;
	private int depth = 0;
	ConfigReader reader;

	public enum ClientOperation {

		HELP {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated
				System.out.println("*                                           *");
				System.out.println("* ls            - list files in directory   *");
				System.out.println("* pwd           - print the CWD             *");
				System.out.println("* mem           - print minion memory usage *");
				System.out.println("* lsm           - list minion locations     *");
				System.out.println("* cd    [dir]   - navigate to directory     *");
				System.out.println("* cat   [file]  - read a file               *");
				System.out.println("* mkdir [file]  - create a directory        *");
				System.out.println("* rm    [file]  - delete a file             *");
				System.out.println("* find  [file]  - find primary minion       *");
				System.out.println("* time  [file]  - get timestamp             *");
				System.out.println("* nano  [file]  - write to file             *");
				System.out.println("* help          - print all commands        *");
			}
		},

		PWD {
			@Override
			public void executeOp(String[] cmds, Client client) {
				System.out.println(client.cwdNode.path);
			}
		},

		LSM {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated
				try {
					List<MinionInfo> list = client.masterLink.getMinionInfoList();
					for (int i = 0; i < list.size(); i++) {
						MinionInfo location = list.get(i);
						System.out.println(
								"ID:" + location.getId() + "@" + location.getAddress() + ":" + location.getPort());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		MEM {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated
				try {
					TreeMap<String, Integer> dist = client.masterLink.getMemoryDistribution();
					for (Entry<String, Integer> entry : dist.entrySet()) {
						System.out.println("ID:" + entry.getKey() + " w/ % Allocation: " + entry.getValue());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		TIME {
			@Override
			public void executeOp(String[] cmds, Client client) {
				// TODO Auto-generated
			}
		},

		FIND {
			@Override
			public void executeOp(String[] cmds, Client client) {

				if (fileNameCheck(cmds, client) == false)
					return;

				try {
					System.out.println("The file: " + cmds[1] + " is located on the following minions:");
					ArrayList<Integer> list = client.masterLink.getAllMinionOwners(cmds[1], client.cwdNode);
					for (int i = 0; i < list.size(); i++) {
						int minionID = list.get(i);
						System.out.println("Minion ID: " + minionID);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		RM {
			@Override
			public void executeOp(String[] cmds, Client client) {

				if (fileNameCheck(cmds, client) == false)
					return;

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

				if (fileNameCheck(cmds, client) == false)
					return;

				ArrayList<String> lines;
				try {
					lines = client.minionLink.readFile(cmds[1], client.cwdNode);
					for (int i = 0; i < lines.size(); i++) {
						System.out.println(lines.get(i));
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},

		LS {
			@Override
			public void executeOp(String[] cmds, Client client) {
				client.updateFileNode();
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
				String filename = client.cwdNode.path + "/" + cmds[1];
				filename = filename.split("tmp")[1];

				try {
					if (client.masterLink.doesFileExist(filename)) {
						System.out.println("Already exists.");
						FileContent content = client.minionLink.getFileContent(cmds[1], client.cwdNode);

						try {
							content.writeByte(cmds[1]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

//					Lease lease = client.masterLink.lease(Integer.toString(client.clientID), filename);

					ProcessBuilder processBuilder = new ProcessBuilder(cmds[0], cmds[1]);
					processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
					processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
					Process p = processBuilder.start();
					p.waitFor();
					
					FileContent content = new FileContent(cmds[1]);
					client.minionLink.writeFile(content, client.cwdNode);
					client.updateFileNode();
					content.delete();

//					System.out.println(lease.getHolder());
//					System.out.println(lease.isExpired());
//					
//					if (lease != null) {
//						if (!lease.isExpired()) {
//							FileContent content = new FileContent(cmds[1]);
//							client.minionLink.writeFile(content, client.cwdNode);
//							client.updateFileNode();
//							content.delete();
//							System.out.println("Lease active - changes pushed through.");
//						} else {
//							System.out.println("Lease on file expired - changes ignored.");
//						}
//					}

				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					System.out.println("exception happened - here's what I know: ");
					e.printStackTrace();
					System.exit(-1);
				}

			}
		};

		public abstract void executeOp(String[] cmds, Client client);
	}

	public Client(String hostname, int port) {

		this.clientMasterStubName = "ClientMasterLink";

		try {
			masterRegistry = LocateRegistry.getRegistry(hostname, port);
			System.out.println("master host name is " + hostname + " and port is " + port);

			masterLink = (ClientMasterLink) masterRegistry.lookup(this.clientMasterStubName);
			System.out.println("Successfully fetched master-server link stub.");

			this.clientID = masterLink.assignClientID();
			System.out.println("Your ID is: " + this.clientID);

			String[] minionInfo = masterLink.getRandomMinionInfo();
			String minionID = minionInfo[0];
			String minionHostname = minionInfo[1];
			String minionPort = minionInfo[2];

			System.out.println("Attempting to connection to minion at registry: " + minionHostname + ":" + minionPort);

			minionRegistry = LocateRegistry.getRegistry(minionHostname, Integer.parseInt(minionPort));

			this.clientMinionStubName = "ClientMinionLink_" + minionID;
			System.out.println(this.clientMinionStubName);
			minionLink = (ClientMinionLink) minionRegistry.lookup(this.clientMinionStubName);
			System.out.println("Successfully fetched minionr-server link stub.");

		} catch (RemoteException | NotBoundException e) {
			System.err.println(e);
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
		ClientOperation operation = null;
		if (cmds[0].equals("exit")) {
			return true;
		} else {
			String op = CommandParser.parse(cmds);
			try {
				operation = ClientOperation.valueOf(op);
			} catch (IllegalArgumentException e) {
				System.out.println("Operation not valid!");
				return false;
			} catch (Exception e) {
				return false;
			}
			operation.executeOp(cmds, this);
			return false;
		}
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

	public static boolean fileNameCheck(String[] cmds, Client client) {
		System.out.println("cmds length" + cmds.length);
		if (cmds[1] == null) {
			System.out.println("Please enter enough commands");
			return false;
		}
		System.out.println("file name " + client.cwdNode.path + "/");
		try {
			if (client.masterLink.doesFileExist((client.cwdNode.path + "/" + cmds[1]).split("tmp")[1])) {
				System.out.println("file exits");
				return true;
			} else {
				System.out.println("Please enter a valid file name");
				return false;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

}
