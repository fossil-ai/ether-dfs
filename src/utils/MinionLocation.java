package utils;

import java.io.Serializable;

public class MinionLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3209888687514514650L;

	private int id;
	private String address;
	private int port;
	private String directory;

	private boolean alive;
	private double memory;

	public MinionLocation(int id, String address, int port, String directory, boolean alive) {
		this.id = id;
		this.address = address;
		this.port = port;
		this.directory = directory;
		this.alive = alive;
		this.memory = 0;
	}

	public boolean isAlive() {
		return alive;
	}

	public int getId() {
		return id;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public String getAddress() {
		return this.address;
	}
	
	public int getPort() {
		return this.port;
	}

	public double getMemory() {
		return memory;
	}

	public void setMemory(double memSpace) {
		this.memory = memSpace;
	}

}
