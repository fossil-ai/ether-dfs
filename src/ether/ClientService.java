package ether;

import java.util.Scanner;
import javax.naming.*;

import utils.ConfigReader; 

public class ClientService {
	
	private static void printOptions(){
		
		System.out.println("1. Create a File");
		System.out.println("2. Read a File");
		System.out.println("3. Update a File");
		System.out.println("4. Delete a File");
		System.out.println("5. Request a File's Location");
		System.out.println("6. Close Session");
	}
	

	public static void main(String[] args) {
		
		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		int REG_PORT = reader.getRegistryPort();
		String MS_JUMPLINKNAME = reader.getRegistryJumpName();
		
		Client client = new Client(REG_ADDR, REG_PORT, MS_JUMPLINKNAME);
		
		System.out.println("Welcome to Ether-DFS - What would you like to do?");
		printOptions();

		Scanner scanner = new Scanner(System.in);
	    boolean done = false;
	    
	    while(!done) {
	    	
	        String input = scanner.nextLine();
	        
	        if(input == "1") {
	        	System.out.println("What is the name of the file?");
	            String filename = scanner.nextLine();
	            client.createFile(filename);
	        }
	        else if (input == "2") {
	        	System.out.println("What is the name of the file?");
	            String filename = scanner.nextLine();
	            client.readFile(filename);
	        }
	        else if (input == "3") {
	        	
	        }
	        else if (input == "4") {
	        	
	        }
	        else if (input == "5") {
	        	
	        }
	        else if (input == "6") {
	        	System.out.println("Closing Session. Goodbye.");
	            scanner.close();
	            done = true;
	        }
	        else {
	        	System.out.println("Invalid selection! Try again:");
		        printOptions();
	        	
	        }
		    
	    }
	}

}
