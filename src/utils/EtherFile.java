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
	
	private String name;
	private byte[] data;
	private File file;
	
	public EtherFile(String name) {
		this(name, null);
	}
	
	public EtherFile(String name, byte[] data) {
		this.name = name;
		this.file = new File(name);
		this.data = data;
		
		if (this.data != null) {
			try { 
	            OutputStream os = new FileOutputStream(file); 
	            os.write(data); 
	            os.close(); 
	        } 
	        catch (Exception e) { 
	            System.out.println("Exception: " + e); 
	        }
		}
	}
	
	public String getFileName() {
		return name;
	}
	public void setFileName(String name) {
		this.name = name;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

}
