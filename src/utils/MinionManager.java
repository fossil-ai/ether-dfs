package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MinionManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -134611602620742089L;
	private List<MinionInfo> minionInfoList;
	private Map<String, MinionInfo> minionInfoMap;
	private String minionMetaFile;
	BufferedReader reader;

	public MinionManager() {
		this.minionInfoList = new ArrayList<MinionInfo>();
		this.minionInfoMap = new TreeMap<String, MinionInfo>();
//		this.minionMetaFile = "resources/minionmeta.conf";
//		this.parseMinionMeta();
	}

//	private void parseMinionMeta() {
//
//		try {
//			reader = new BufferedReader(new FileReader(this.minionMetaFile));
//			String currentLine;
//			while ((currentLine = reader.readLine()) != null) {
//				String[] split = currentLine.split(" ");
//				this.idMapping.put(split[0], Integer.valueOf(split[1]));
//				Map<String, String> info = new TreeMap<String, String>();
//				info.put("hostname", split[0].split(":")[0]);
//				info.put("port", split[0].split(":")[1]);
//				this.minionConnectionInfo.put(Integer.valueOf(split[1]), info);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

//	public String[] getMinionInfo(String hostname, String port) {
//		String text = hostname + ":" + port;
//		String[] info = new String[2];
//		BufferedWriter bw = null;
//
//		if (this.idMapping.containsKey(text)) {
//			System.out.println("Minion Exists");
//			info[0] = Integer.toString(this.idMapping.get(text));
//			info[1] = "/tmp/minion_" + info[0];
//		} else {
//
//			File file = new File(this.minionMetaFile);
//			try {
//				if (!file.exists()) {
//					file.createNewFile();
//				}
//				FileWriter fw;
//				fw = new FileWriter(file, true);
//				bw = new BufferedWriter(fw);
//				if (this.idMapping.size() > 0)
//					fw.write(System.getProperty("line.separator"));
//				bw.write(text + " " + this.idMapping.size());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				try {
//					if (bw != null)
//						bw.close();
//				} catch (Exception ex) {
//					System.out.println("Error in closing the BufferedWriter" + ex);
//				}
//			}
//
//			this.idMapping.put(text, this.idMapping.size());
//			info[0] = Integer.toString(this.idMapping.get(text));
//			info[1] = "/tmp/minion_" + info[0];
//
//			System.out.println("File written Successfully");
//		}
//
//		return info;
//
//	};

	public MinionInfo getMinionInfo(String id) {
		return this.minionInfoMap.get(id);
	}

	public String getMinionDirectory(String id) {
		MinionInfo info = this.minionInfoMap.get(id);
		return info.getDirectory();
	}

	public String getMinionHost(String id) {
		MinionInfo info = this.minionInfoMap.get(id);
		return info.getAddress();
	}

	public int getMinionPort(String id) {
		MinionInfo info = this.minionInfoMap.get(id);
		return info.getPort();
	}

	public void addMinion(MinionInfo minionLocation) {
		this.minionInfoList.add(minionLocation);
	}

	public int minionsNum() {
		return this.minionInfoList.size();
	}

	public List<MinionInfo> getMinionLocations() {
		return this.minionInfoList;
	}

	public static void main(String[] args) {

		MinionManager manager = new MinionManager();

	}

}
