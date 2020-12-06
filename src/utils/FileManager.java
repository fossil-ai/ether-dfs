package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

	private Map<String, List<MinionInfo>> fileLocationMap;
	private Map<String, MinionInfo> filePrimaryMinionMap;

	public FileManager() {
		this.fileLocationMap = new HashMap<String, List<MinionInfo>>();
		this.filePrimaryMinionMap = new HashMap<String, MinionInfo>();
	}

	public String getGlobalPath(String filename) {
		return filename;
	}

	public void assignPrimaryMinionToFile(String filename, int primaryMinionIndex, List<MinionInfo> minionLocations) {
		filePrimaryMinionMap.put(filename, minionLocations.get(primaryMinionIndex));
	}

	public void assignSelectedMinionsToFile(String filename, List<MinionInfo> selectedMinions) {
		fileLocationMap.put(filename, selectedMinions);
	}

	public MinionInfo getPrimaryFileLocation(String fileName) {
		return filePrimaryMinionMap.get(fileName);
	}

	public List<MinionInfo> getAllFileLocation(String fileName) {
		return fileLocationMap.get(fileName);
	}

}
