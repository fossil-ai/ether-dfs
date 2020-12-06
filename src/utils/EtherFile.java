package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.TreeMap;

public class EtherFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7610688103888633848L;
	
	private int version;
	private TreeMap<String,Integer> fileMap;
	
	public EtherFile() {
		this.fileMap = new TreeMap<String,Integer>();
		}
	
	public TreeMap getFileMap() {
		return this.fileMap;
		}
}
