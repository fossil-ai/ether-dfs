package ether;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Minion extends UnicastRemoteObject implements FileSystem {
	
	public String name;
	public float load;
	
	protected Minion() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create(String filename, int data) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String destination, long size) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	

}
