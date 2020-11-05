package ether;

import java.util.Scanner;
import javax.naming.*; 

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
		
		/*
		 * Provide the client object with:
		 * 1. Java RMI Registry port #
		 * 2. Java RMI Registry hostname
		 * 3. Name of the master server's remote object reference in registry.
		 * */
		
		final String REG_ADDR = "localhost";
		final int REG_PORT = 50904;
		final String MS_LINKNAME = "MasterLink";
		
		Client client = new Client(REG_ADDR, REG_PORT, MS_LINKNAME);
		
		System.out.println("Welcome to Ether-DFS - What would you like to do?");
		printOptions();

		Scanner scanner = new Scanner(System.in);
	    boolean done = false;
	    
	    while(!done) {
	    	
	        String input = scanner.nextLine();
	    	
		    switch (input) {
		        case "1":
		            System.out.println("What is the name of the file?");
		            String filename = scanner.nextLine();
		            client.createFile(filename);
		            break;
	
		        case "2":
		            System.out.println("What is the name of the file?");
		            break;
	
		        case "3":
		            System.out.println("What is the name of the file?");
		            break;
	
		        case "4":
		        	System.out.println("What is the name of the file?");
		            break;
	
		        case "5":
		        	System.out.println("What is the name of the file?");
		            break;
		        
		        case "6":
		            System.out.println("Closing Session. Goodbye.");
		            scanner.close();
		            done = true;
		            break;
	
		        default:
		            System.out.println("Invalid selection! Try again:");
		            printOptions();
		   }
		    
	    }
	}

}
