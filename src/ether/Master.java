package ether;

import java.rmi.*;  
import java.rmi.server.*;  

public class Master extends UnicastRemoteObject implements FileSystem {
	
	public int replication_factor = 2;
	public float block_size;
	public String[] minion_addresses;
	
	protected Master() throws RemoteException {
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
