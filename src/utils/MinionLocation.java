package utils;

import java.io.Serializable;

public class MinionLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3209888687514514650L;
	
	private String address;
	private int id;
	private boolean alive;
	
	public MinionLocation(int id, String address, boolean alive) {
		this.id = id;
		this.address = address;
		this.alive = alive;
	}
	
	boolean isAlive(){
		return alive;
	}
	
	int getId(){
		return id;
	}
	
	void setAlive(boolean alive){
		this.alive = alive;
	}
	
	String getAddress(){
		return address;
	}

}
