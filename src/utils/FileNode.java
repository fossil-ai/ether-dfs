package utils;

import java.util.TreeMap;

public class FileNode {
	
	public String filename;
	public String path;
	public FileNode parent;
	public TreeMap<String, FileNode> children;
	public boolean isDir = false;

}
