package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class EtherFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7610688103888633848L;
	
	private int version;
	private Map<String,Integer> fileMap;
	
	public EtherFile() {
		this.fileMap = new HashMap<String,Integer>();
		}
	
	public EtherFile() {
		this.fileMap = new HashMap<String,Integer>();
		}
}
