package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ConfigReader {
	
	private Map<String, String> configs;
	private BufferedReader reader;
	
	public ConfigReader(){
		
		try {
			reader = new BufferedReader(new FileReader("resources/filesys.conf"));
			configs = new TreeMap<String, String>();
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
	
	public int getRegistryPort(){
		return Integer.parseInt(configs.get("REG_PORT"));
	}
	
	public String getRegistryHost(){
		return configs.get("REG_HOST");
	}
	
	public String getRegistryJumpName(){
		return configs.get("MS_JUMPLINK");
	}
	
	public int getMinionNum(){
		return Integer.parseInt(configs.get("MINION_NUMS"));
	}
	
	public String getMinions(){
		return configs.get("MINION_ADDRESSES");
	}
	
	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
		System.out.println(reader.getRegistryPort());
		System.out.println(reader.getRegistryHost());
		System.out.println(reader.getRegistryJumpName());
		System.out.println(reader.getMinionNum());
		System.out.println(reader.getMinions());
	}


}


