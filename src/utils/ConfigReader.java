package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ConfigReader {

	private Map<String, String> configs;
	private BufferedReader reader;
	
	public ConfigReader() {

		try {
			reader = new BufferedReader(new FileReader("resources/filesys.conf"));
			configs = new TreeMap<String, String>();
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] config = currentLine.split(" ");
				configs.put(config[0], config[1]);
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

	public String getRegistryClientJumpName() {
		return configs.get("MS_CLIENT_JUMPLINK");
	}

	public String getRegistryMinionJumpName() {
		return configs.get("MS_MINION_JUMPLINK");
	}

	public int getMinionNum() {
		return Integer.parseInt(configs.get("MINION_NUMS"));
	}

	public String getMinion1Addr() {
		return configs.get("MINION_ADDRESS1");
	}
	public String getMinion2Addr() {
		return configs.get("MINION_ADDRESS2");
	}
	public String getMinion3Addr() {
		return configs.get("MINION_ADDRESS3");
	}
	public int getMinion1Port() {
		return Integer.parseInt(configs.get("MINION_PORT1"));
	}
	public int getMinion2Port() {
		return Integer.parseInt(configs.get("MINION_PORT2"));
	}
	public int getMinion3Port() {
		return Integer.parseInt(configs.get("MINION_PORT3"));
	}


	public static void main(String[] args) {
		ConfigReader reader = new ConfigReader();
		System.out.println(reader.getRegistryPort());
		System.out.println(reader.getRegistryHost());
		System.out.println(reader.getRegistryClientJumpName());
		System.out.println(reader.getRegistryMinionJumpName());
		System.out.println(reader.getMinionNum());
		System.out.println(reader.getMinion1Addr());
		System.out.println(reader.getMinion2Addr());
		System.out.println(reader.getMinion3Addr());
	}
}