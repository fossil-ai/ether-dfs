package ether;

import java.util.Scanner;
import javax.naming.*;

import utils.ConfigReader; 

public class ClientService {
	

	public static void main(String[] args) {
		
		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		int REG_PORT = reader.getRegistryPort();
		String MS_JUMPLINKNAME = reader.getRegistryClientJumpName();
		
		System.out.println("********* ******************** ********");
		System.out.println("********* WELCOME TO ETHER-DFS ********");
		System.out.println("********* ******************** ********");
		
		Client client = new Client(REG_ADDR, REG_PORT, MS_JUMPLINKNAME);

		Scanner scanner = new Scanner(System.in);
	    boolean done = false;
	    
	    while(!done) {
	    	client.printCWD();
	        String command = scanner.nextLine();
	        String[] commandArr = command.split(" ");
	        done = client.execute(commandArr);
	    }
	    
	    System.out.println("Quitting Ether-DFS. Goodbye!");
	}

}
