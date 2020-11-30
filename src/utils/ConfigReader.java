package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ConfigReader {

	private Map<String, String> configs;
	private Map<String, Map<String, String>> minionConfigs;
	private BufferedReader reader;

	public ConfigReader() {

		try {
			reader = new BufferedReader(new FileReader("resources/filesys.conf"));
			configs = new TreeMap<String, String>();
			minionConfigs = new TreeMap<String, Map<String, String>>();
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				System.out.println(currentLine);
				String[] config = currentLine.split(" ");
				if(config[0].equalsIgnoreCase("MINION_ADDRESS_PORT")) {
					String[] info = config[1].split(",");
					TreeMap<String, String> minion_info = new TreeMap<String, String>();
					minion_info.put("minionHost", info[1]);
					minion_info.put("minionPort", info[2]);
					this.minionConfigs.put(info[0], minion_info);
				}
				else {
					configs.put(config[0], config[1]);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getRegistryPort() {
		return Integer.parseInt(configs.get("REG_PORT"));
	}

	public String getRegistryHost() {
		return configs.get("REG_ADDRESS");
	}


	public int getMinionNum() {
		return Integer.parseInt(configs.get("MINION_NUMS"));
	}
	
	public String getMinionHost(String id) {
		return this.minionConfigs.get(id).get("minionHost");
	}
	
	public int getMinionPort(String id) {
		return Integer.parseInt(this.minionConfigs.get(id).get("minionPort"));
	}


	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
		System.out.println(reader.getRegistryPort());
		System.out.println(reader.getRegistryHost());
		System.out.println(reader.getMinionNum());
		
		System.out.println(reader.getMinionHost("0"));
		System.out.println(reader.getMinionPort("0"));
		
		System.out.println(reader.getMinionHost("1"));
		System.out.println(reader.getMinionPort("1"));
		
		System.out.println(reader.getMinionHost("2"));
		System.out.println(reader.getMinionPort("2"));
		
		System.out.println(reader.getMinionHost("3"));
		System.out.println(reader.getMinionPort("3"));
	}
}