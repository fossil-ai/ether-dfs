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
	
	public boolean isAlive(){
		return alive;
	}
	
	public int getId(){
		return id;
	}
	
	public void setAlive(boolean alive){
		this.alive = alive;
	}
	
	public String getAddress(){
		return address;
	}

}
