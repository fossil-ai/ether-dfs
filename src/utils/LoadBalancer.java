package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.TreeMap;

public class LoadBalancer {
	
	public long globalMemory;
	private TreeMap<String, Double> minionMemoryMap;
	private TreeMap<String, Integer> minionDistMap;
	private TreeMap<String, Integer> loadStatus; 
	private int[] bounds = {20, 80};
	
	public LoadBalancer(){
		this.globalMemory = 0;
		this.minionMemoryMap = new TreeMap<String, Double>();
		this.minionDistMap = new TreeMap<String, Integer>();
		this.loadStatus = new TreeMap<String, Integer>();
	}
	
	public void updateMemoryStats(String id, double size){
		this.minionMemoryMap.put(id, size);
		this.loadStatus.put(id, 0);
		this.updateGlobal();
		this.assignStatus();
	}
	
	public TreeMap<String, Integer> getMemDist(){
		return this.minionDistMap;
	}
	
	private void assignStatus(){
		for (Entry<String, Double> entry : this.minionMemoryMap.entrySet()) {
			System.out.println("Minion with ID " + entry.getKey() + " has size " + entry.getValue());
			int percentage = (int) (100.0 * (entry.getValue() / this.globalMemory));
			this.minionDistMap.put(entry.getKey(), percentage);
			if(bounds[0] < this.minionDistMap.get(entry.getKey()) && this.minionDistMap.get(entry.getKey()) < bounds[1]){
				this.loadStatus.put(entry.getKey(), 0);
			}
			else if (bounds[0] >= this.minionDistMap.get(entry.getKey())) {
				this.loadStatus.put(entry.getKey(), -1);
			}
			else {
				this.loadStatus.put(entry.getKey(), 1);
			}
		}
	}
	
	private void updateGlobal(){
		this.globalMemory = 0;
		for (Entry<String, Double> entry : this.minionMemoryMap.entrySet()) {
			this.globalMemory += entry.getValue();
		}
		System.out.println("Global is now: " + this.globalMemory);
	}
	
	
	
	

}
