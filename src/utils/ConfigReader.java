package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class ConfigReader {
	
	/*
	 * Parameters
	 * - Port of Registry
	 * - 
	 * - Number of Minions
	 * */
	private Map<String, String> configs;
	private BufferedReader reader;
	
	public ConfigReader(){
		try {
			reader = new BufferedReader(new FileReader("resources/filesys.conf"));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				String[] config = currentLine.split(" ");
				configs.put(config[0], config[1]);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getRegistryPort(){
		return configs.get("REG_PORT");
	}
	
	public String getRegistryHost(){
		return configs.get("REG_PORT");
	}
	
	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
	
	}


}


