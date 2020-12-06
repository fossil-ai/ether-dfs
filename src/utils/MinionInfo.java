package utils;

import java.io.Serializable;

public class MinionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3209888687514514650L;

	private int id;
	private int port;
	private String address;
	private String directory;
	private boolean alive;

	public MinionInfo(int id, String address, int port, String directory, boolean alive) {
		this.id = id;
		this.address = address;
		this.port = port;
		this.directory = directory;
		this.alive = alive;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public int getId() {
		return id;
	}

	public String getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}

	public String getDirectory() {
		return this.directory;
	}

}
