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
	private Map<String, MinionInfo> activeMinionMap;
	private Map<String, MinionInfo> minionInfoMap;
	private Map<String, Integer> idMapping;
	private String minionMetaFile;
	BufferedReader reader;

	public MinionManager() {
		this.activeMinionMap = new TreeMap<String, MinionInfo>();
		this.minionInfoMap = new TreeMap<String, MinionInfo>();
		this.idMapping = new TreeMap<String, Integer>();
		this.minionMetaFile = "resources/minionmeta_local.conf";
		this.parseMinionMeta();
	}

	private void parseMinionMeta() {

		try {
			reader = new BufferedReader(new FileReader(this.minionMetaFile));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] split = currentLine.split(" ");
				int id = Integer.parseInt(split[1]);
				String hostname = split[0].split(":")[0];
				int port = Integer.parseInt(split[0].split(":")[1]);
				String directory = "tmp/minion_" + id;
				boolean alive = false;
				MinionInfo info = new MinionInfo(id, hostname, port, directory, alive);
				this.minionInfoMap.put(Integer.toString(id), info);
				this.idMapping.put(hostname + port, id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getID(String code) {
		return this.idMapping.get(code);
	}

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

	public void addMinion(MinionInfo info) {
		MinionInfo info_ = this.minionInfoMap.get(Integer.toString(info.getId()));
		info_.setAlive(true);
		this.activeMinionMap.put(Integer.toString(info.getId()), info_);
	}

	public void removeMinion(MinionInfo info) {
		MinionInfo info_ = this.minionInfoMap.get(Integer.toString(info.getId()));
		info_.setAlive(false);
		this.activeMinionMap.remove(Integer.toString(info.getId()));
	}

	public int minionsNum() {
		return this.activeMinionMap.size();
	}

	public List<MinionInfo> getMinionInfoList() {
		List<MinionInfo> list = new ArrayList<MinionInfo>(this.activeMinionMap.values());
		return list;
	}

	public static void main(String[] args) {

		MinionManager manager = new MinionManager();

	}

}
