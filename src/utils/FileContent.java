package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class FileContent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6709795798632515110L;
	private String filename;
	private byte[] bytes;
	private File file;
	
	public FileContent(String filename) {
		// file to byte[], File -> Path
		  file = new File(filename);
		  try {
			byte[] bytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	// Method which write the bytes into a file 
//    static void writeByte(byte[] bytes) 
//    { 
//        try { 
//  
//            // Initialize a pointer 
//            // in file using OutputStream 
//            OutputStream 
//                os 
//                = new FileOutputStream(file); 
//  
//            // Starts writing the bytes in it 
//            os.write(bytes); 
//            System.out.println("Successfully"
//                               + " byte inserted"); 
//  
//            // Close the file 
//            os.close(); 
//        } 
//  
//        catch (Exception e) { 
//            System.out.println("Exception: " + e); 
//        } 
//    } 
//	
	
	

}
