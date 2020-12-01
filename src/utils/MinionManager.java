package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MinionManager {

	private List<MinionLocation> minionLocations;
	private Map<String, Integer> idMapping;
	private Map<Integer, Map<String, String>> minionConnectionInfo;
	private String minionMetaFile;
	BufferedReader reader;

	public MinionManager() {
		this.minionLocations = new ArrayList<MinionLocation>();
		this.idMapping = new TreeMap<String, Integer>();
		this.minionConnectionInfo = new TreeMap<Integer, Map<String, String>>();
		this.minionMetaFile = "resources/minionmeta.conf";
		this.parseMinionMeta();
	}

	private void parseMinionMeta() {

		try {
			reader = new BufferedReader(new FileReader(this.minionMetaFile));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] split = currentLine.split(" ");
				this.idMapping.put(split[0], Integer.valueOf(split[1]));
				Map<String, String> info = new TreeMap<String, String>();
				info.put("hostname", split[0].split(":")[0]);
				info.put("port", split[0].split(":")[1]);
				this.minionConnectionInfo.put(Integer.valueOf(split[1]), info);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String[] getMinionInfo(String hostname, String port) {
		String text = hostname + ":" + port;
		String[] info = new String[2];
		BufferedWriter bw = null;

		if (this.idMapping.containsKey(text)) {
			System.out.println("Minion Exists");
			info[0] = Integer.toString(this.idMapping.get(text));
			info[1] = "/tmp/minion_" + info[0];
		} else {

			File file = new File(this.minionMetaFile);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw;
				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				if (this.idMapping.size() > 0)
					fw.write(System.getProperty("line.separator"));
				bw.write(text + " " + this.idMapping.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();
				} catch (Exception ex) {
					System.out.println("Error in closing the BufferedWriter" + ex);
				}
			}

			this.idMapping.put(text, this.idMapping.size());
			info[0] = Integer.toString(this.idMapping.get(text));
			info[1] = "/tmp/minion_" + info[0];

			System.out.println("File written Successfully");
		}

		return info;

	};

	public String getMinionHost(String id) {
		return this.minionConnectionInfo.get(Integer.parseInt(id)).get("hostname");
	}

	public int getMinionPort(String id) {
		return Integer.parseInt(this.minionConnectionInfo.get(Integer.parseInt(id)).get("port"));
	}

	public void addMinion(MinionLocation minionLocation) {
		this.minionLocations.add(minionLocation);
	}

	public int minionsNum() {
		return this.minionLocations.size();
	}

	public List<MinionLocation> getMinionLocations() {
		return minionLocations;
	}

	public void setMinionLocations(List<MinionLocation> minionLocations) {
		this.minionLocations = minionLocations;
	}

	public static void main(String[] args) {

		MinionManager manager = new MinionManager();
//		manager.getMinionInfo("localhost", "59602");
//		manager.getMinionInfo("localhost", "59601");
		for (int i = 0; i < manager.idMapping.size(); i++) {
			System.out.println(manager.idMapping.toString());
		}

	}

}
