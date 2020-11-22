package utils;

import java.io.Serializable;

public class MinionLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3209888687514514650L;
	
	private static String address;
	private int id;
	private boolean alive;
	private double memSpace;
	
	public MinionLocation(int id, String address, boolean alive, double memSpace) {
		this.id = id;
		this.address = address;
		this.alive = alive;
		this.memSpace = memSpace; // init to 0 = space is empty;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public int getId(){
		return id;
	}
	
	public void setAlive(boolean alive){
		this.alive = alive;
	}
	
	public static String getAddress(){
		return address;
	}
	
	public double getMemSpace() {
		return memSpace;
	}
	
	public void setMemSpace( double memSpace) {
		this.memSpace = memSpace;
	}

}
