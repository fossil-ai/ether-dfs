package ether;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import utils.ConfigReader;

public class ClientService {

	public static void main(String[] args) {

		ConfigReader reader = new ConfigReader();
		String REG_ADDR = reader.getRegistryHost();
		int REG_PORT = reader.getRegistryPort();

		System.out.println("********* ************************** ********");
		System.out.println("********* ** WELCOME TO ETHER-DFS ** ********");
		System.out.println("********* ************************** ********");
		System.out.println("********* ** SUPPORTED OPERATIONS ** ********");
		System.out.println("********* ************************** ********");
		System.out.println("*                                           *");
		System.out.println("* ls            - list files in directory   *");
		System.out.println("* pwd           - print the CWD             *");
		System.out.println("* mem           - print minion memory usage *");
		System.out.println("* lsm           - list minion locations     *");
		System.out.println("* cd    [dir]   - navigate to directory     *");
		System.out.println("* cat   [file]  - read a file               *");
		System.out.println("* mkdir [file]  - create a directory        *");
		System.out.println("* rm    [file]  - delete a file             *");
		System.out.println("* find  [file]  - find primary minion       *");
		System.out.println("* nano  [file]  - write to file             *");
		System.out.println("* help          - print all commands        *");
		System.out.println("*                                           *");
		System.out.println("********* ************************** ********");

		Client client = new Client(REG_ADDR, REG_PORT);
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(client, 5, 20, TimeUnit.SECONDS);

		Scanner scanner = new Scanner(System.in);
		boolean done = false;

		while (!done) {
			client.printCWD();
			String command = scanner.nextLine();
			String[] commandArr = command.split(" ");
			done = client.execute(commandArr);
		}

		scanner.close();
		System.out.println("Quitting Ether-DFS. Goodbye!");
	}

}
