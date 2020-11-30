package utils;

import java.io.Serializable;

public class FileTree implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -808762319040839381L;
	private int id;
	private byte[] treeData;
	
	public FileTree(int minion_id){
		this.id = minion_id;
	}
	
	public void setData(byte[] data){
		this.treeData = data.clone();
	}
	
	public byte[] getData(){
		return this.treeData;
	}
	
	public int getID() {
		return this.id;
	}

}
