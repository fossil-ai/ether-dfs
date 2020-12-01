package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.TreeMap;
import java.util.TreeSet;

public class LoadBalancer {
	
	public long globalMemory;
	private TreeMap<String, Double> minionMemoryMap;
	private TreeMap<String, Integer> minionDistMap;
	private TreeMap<String, Integer> loadStatus;
	private TreeMap<Integer, TreeSet<String>> loadToMinionMap;
	private int[] bounds = {20, 80};
	
	public LoadBalancer(){
		this.globalMemory = 0;
		this.minionMemoryMap = new TreeMap<String, Double>();
		this.minionDistMap = new TreeMap<String, Integer>();
		this.loadStatus = new TreeMap<String, Integer>();
		this.loadToMinionMap = new TreeMap<Integer, TreeSet<String>>();
		this.loadToMinionMap.put(0, new TreeSet<String>());
		this.loadToMinionMap.put(1, new TreeSet<String>());
		this.loadToMinionMap.put(-1, new TreeSet<String>());
	}
	
	public int updateMemoryStats(String id, double size){
		this.minionMemoryMap.put(id, size);
		this.loadStatus.put(id, 0);
		this.updateGlobal();
		this.assignStatus();
		return this.loadStatus.get(id);
	}
	
	// Send back random minionID of under-loaded minion, if not normal loaded minion, if not return -1
	public int getNonOverloadedMinion(){
		if(this.loadToMinionMap.get(0).size() > 0){
			int size = this.loadToMinionMap.get(0).size();
			int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
			int i = 0;
			for(String id : this.loadToMinionMap.get(0)){
			    if (i == item) {
			    	return Integer.parseInt(id);
			    }
			    i++;
			}
		}
		else if(this.loadToMinionMap.get(1).size() > 0){
			int size = this.loadToMinionMap.get(0).size();
			int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
			int i = 0;
			for(String id : this.loadToMinionMap.get(0)){
			    if (i == item) {
			    	return Integer.parseInt(id);
			    }
			    i++;
			}	
		}
		return -1;
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
				this.loadToMinionMap.get(0).add(entry.getKey());
				if(this.loadStatus.get(entry.getKey()) != 0){
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
				}
			}
			else if (bounds[0] >= this.minionDistMap.get(entry.getKey())) {
				this.loadStatus.put(entry.getKey(), -1);
				this.loadToMinionMap.get(-1).add(entry.getKey());
				if(this.loadStatus.get(entry.getKey()) != -1){
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
				}
			}
			else {
				this.loadStatus.put(entry.getKey(), 1);
				this.loadToMinionMap.get(1).add(entry.getKey());
				if(this.loadStatus.get(entry.getKey()) != 1){
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
				}
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
