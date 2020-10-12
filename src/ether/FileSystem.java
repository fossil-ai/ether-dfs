package ether;
import java.rmi.*;  


public interface FileSystem extends Remote {
	
	public void create(String filename, int data) throws RemoteException;
	
	public void read(String filename) throws RemoteException;
	
	public void write(String destination, long size) throws RemoteException;
	
	public void open() throws RemoteException;
	
	public void close() throws RemoteException;

}
