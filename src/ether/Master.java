package ether;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import links.MasterMinionLink;
import links.MinionMasterJumpLink;
import links.MinionMasterLink;
import utils.ClientManager;
import utils.FileManager;
import utils.FileNode;
import utils.GlobalNameSpaceManager;
import utils.LocalNameSpaceManager;
import utils.MinionLocation;
import utils.MinionManager;
import utils.NameSpaceSynchronizer;
import links.ClientMasterJumpLink;
import links.ClientMasterLink;

public class Master extends UnicastRemoteObject
		implements MinionMasterLink, ClientMasterLink, ClientMasterJumpLink, MinionMasterJumpLink {

	public String name;
	public String address;

	FileManager fileManager;
	MinionManager minionManager;
	ClientManager clientManager;
	GlobalNameSpaceManager globalNameSpaceManager;
	NameSpaceSynchronizer nameSpaceSynchronizer;

	private ServerSocket serverSocket;
	private Socket socket;
	private int port = 50000;
	Map<String, Double> MinionsList = new HashMap<String, Double>();

	Random random;

	public Master() throws RemoteException {
		this.fileManager = new FileManager();
		this.globalNameSpaceManager = new GlobalNameSpaceManager();
		this.nameSpaceSynchronizer = new NameSpaceSynchronizer(this.globalNameSpaceManager);
		this.minionManager = new MinionManager();
		this.clientManager = new ClientManager();
		this.random = new Random();

		/*
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("server socket started!!!!");
			while(true) {

				socket = serverSocket.accept();
				System.out.println("connection established!!!!");
				threadServer thread = new threadServer(socket);
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// include all the minions IP address; initialize minions memory space to 1;
		// MinionsList.put("172.31.33.125", 1);
		MinionsList.put("172.31.46.197", 1.00);
		 */
	}

	@Override
	public void createFile(String filename) throws AccessException, RemoteException, NotBoundException {
		System.out.println("Master: File Created");
		for (int i = 0; i < this.minionManager.minionsNum(); i++) {
			try {
				// if( this.minionManager.getMinionLocations().get(i).getMemSpace() > 0.8) i ++;
				this.minionManager.getMinionMasterInvocation().get(i).createFile(filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		int primaryReplicaIndex = 0;
//		this.minionManager.getMinionMasterInvocation().get(primaryReplicaIndex).takeCharge(filename,
//				this.minionManager.getMinionLocations());
//		this.fileManager.assignSelectedMinionsToFile(filename, this.minionManager.getMinionLocations());
//		this.fileManager.assignPrimaryMinionToFile(filename, primaryReplicaIndex,
//				this.minionManager.getMinionLocations());

	}

	@Override
	public MinionLocation locatePrimaryMinion(String fileName) throws RemoteException {
		return fileManager.getPrimaryFileLocation(fileName);
	}

	public String assignMinionToClient(int clientID) {
		System.out.println("Master: Assigning minion to client");
		if (this.minionManager.minionsNum() < 1) {
			System.out.println("No minion active in the minion manager - are any minions active?");
			return "FAILED TO ASSIGN";
		}
		try {
			this.minionManager.getMinionMasterInvocation().get(0).addClientToMinion(clientID, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		return "";
	}

	@Override
	public int getMinionCount() {
		return this.minionManager.minionsNum();
	}

	@Override
	public int getClientCount() {
		return this.clientManager.clientsNum();
	}

	public void listenHeartBeat() {
		class threadServer extends Thread {
			Socket threadSocket;
			MinionLocation minionLocation;

			public threadServer(Socket threadSocket) {
				this.threadSocket = threadSocket;
			}

			public void run() {
				double percent;
				String tempAddress;
				try {
					DataInputStream input = new DataInputStream(threadSocket.getInputStream());
					tempAddress = threadSocket.getRemoteSocketAddress().toString();
					System.out.println("input is " + input + "address is " + tempAddress);
					if (minionLocation.getAddress() == tempAddress) {
						minionLocation.setAlive(true);
						minionLocation.setMemSpace(input.readDouble());
					} else {
						System.out.println(minionLocation.getId() + "minions is down, " + minionLocation.getAddress()
								+ " remove from the list");
						minionLocation.setAlive(false);
						minionLocation.setMemSpace(1); // full space, do not write to this server.
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	@Override
	public String clientJumpStart(Registry registry) {
		System.out.println("HEY! Connecting new client...");
		System.out.println("Looks like there are " + this.getClientCount() + " clients connected to the DFS...");
		System.out.println("Assigning client with ID: " + this.getClientCount() + 1);
		int id = this.getClientCount() + 1;

		try {
			registry.rebind("ClientMasterLink_" + id, (ClientMasterLink) UnicastRemoteObject.toStub(this));
			System.out.println("ClientMasterLink rebind success");
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "ClientMasterLink_" + id;
	}

	@Override
	public String minionJumpStart(Registry registry) {
		System.out.println("HEY! Connecting new minion...");
		System.out.println("Looks like there are " + this.getMinionCount() + " minions connected...");
		System.out.println("Assigning minion with ID: " + this.getMinionCount());
		int id = this.getMinionCount();

		try {
			registry.rebind("MinionMasterLink_" + id, (MinionMasterLink) UnicastRemoteObject.toStub(this));
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "MinionMasterLink_" + id;
	}

	@Override
	public ArrayList<String> listFilesAtCWD(FileNode cwdNode) {
		ArrayList<String> listOfFiles = new ArrayList<String>();
		for (Map.Entry<String, FileNode> entry : cwdNode.children.entrySet()) {
			listOfFiles.add(entry.getValue().filename);
		}
		return listOfFiles;
	}

	@Override
	public FileNode getRootNode() {
		return this.globalNameSpaceManager.getRoot();
	}

	@Override
	public void storeMinionLocation(MinionLocation location) throws RemoteException {
		// TODO Auto-generated method stub
		this.minionManager.addMinion(location);
	}

	@Override
	public void synchronize(String id, LocalNameSpaceManager nsManager) throws RemoteException {
		// TODO Auto-generated method stub
		this.nameSpaceSynchronizer.synchronize(id, nsManager);
		this.nameSpaceSynchronizer.buildGlobalNameSpace();
		this.globalNameSpaceManager.rebuildGlobalPath();
	}

	@Override
	public String getRandomMinionID() throws RemoteException {
		int randID = ThreadLocalRandom.current().nextInt(0, this.getMinionCount());
		return Integer.toString(randID);
	}

	@Override
	public void registryBind (Registry registry , String name, MasterMinionLink link) {
		try {
			registry.rebind(name, link);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
