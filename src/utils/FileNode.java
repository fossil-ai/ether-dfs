package utils;

import java.io.Serializable;
import java.util.TreeMap;

public class FileNode implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -562992716721742567L;
	public String filename;
	public String path;
	public FileNode parent;
	public TreeMap<String, FileNode> children;
	public TreeMap<String, FileNode> nodes;
	public boolean isDir = false;

}
