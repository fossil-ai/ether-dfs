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
	private int[] bounds = { 20, 80 };

	public LoadBalancer() {
		this.globalMemory = 0;
		this.minionMemoryMap = new TreeMap<String, Double>();
		this.minionDistMap = new TreeMap<String, Integer>();
		this.loadStatus = new TreeMap<String, Integer>();
		this.loadToMinionMap = new TreeMap<Integer, TreeSet<String>>();
		this.loadToMinionMap.put(0, new TreeSet<String>());
		this.loadToMinionMap.put(1, new TreeSet<String>());
		this.loadToMinionMap.put(-1, new TreeSet<String>());
	}

	public int updateMemoryStats(String id, double size) {
		this.minionMemoryMap.put(id, size);
		this.loadStatus.put(id, -1);
		this.updateGlobal();
		this.assignStatus();
		return this.loadStatus.get(id);
	}

	
	public int getNonOverloadedMinion() {
		System.out.println(this.loadToMinionMap.toString());
		System.out.println(this.loadStatus.toString());
		if (this.loadToMinionMap.get(-1).size() > 0) {
			int size = this.loadToMinionMap.get(-1).size();
			int item = new Random().nextInt(size);
			int i = 0;
			for (String id : this.loadToMinionMap.get(-1)) {
				if (i == item) {
					return Integer.parseInt(id);
				}
				i++;
			}
		} else if (this.loadToMinionMap.get(0).size() > 0) {
			int size = this.loadToMinionMap.get(0).size();
			int item = new Random().nextInt(size);							
			int i = 0;
			for (String id : this.loadToMinionMap.get(0)) {
				if (i == item) {
					return Integer.parseInt(id);
				}
				i++;
			}
		}
		return -1;
	}

	public TreeMap<String, Integer> getMemDist() {
		return this.minionDistMap;
	}

	private void assignStatus() {
	
		System.out.println(this.minionMemoryMap.toString());
		System.out.println(this.minionDistMap.toString());
		System.out.println(this.loadStatus.toString());
		System.out.println(this.loadToMinionMap.toString());
		System.out.println("-----------------------");
		

		
		for (Entry<String, Double> entry : this.minionMemoryMap.entrySet()) {
			System.out.println("Minion with ID " + entry.getKey() + " has size " + entry.getValue());
			int percentage = (int) (100.0 * (entry.getValue() / this.globalMemory));
			this.minionDistMap.put(entry.getKey(), percentage);
			
			if (bounds[0] < this.minionDistMap.get(entry.getKey())
					&& this.minionDistMap.get(entry.getKey()) < bounds[1]) {
				
				
				System.out.println("UPDATING MINION " +  entry.getKey() + " from " + this.loadStatus.get(entry.getKey()) +  " to 0 LOAD STATUS");
					
					
				if (this.loadStatus.get(entry.getKey()) != 0) {
					System.out.println("WASNT THIS BEFORE!");
					System.out.println(this.loadToMinionMap.toString());
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
					System.out.println(this.loadToMinionMap.toString());
					System.out.println("REMOVED");
				}
				
				
				this.loadStatus.put(entry.getKey(), 0);
				this.loadToMinionMap.get(0).add(entry.getKey());
				
			} else if (bounds[0] >= this.minionDistMap.get(entry.getKey())) {
				System.out.println("UPDATING MINION " +  entry.getKey() + " from " + this.loadStatus.get(entry.getKey()) +  " to -1 LOAD STATUS");
				if (this.loadStatus.get(entry.getKey()) != -1) {
					System.out.println("WASNT THIS BEFORE!");
					System.out.println(this.loadToMinionMap.toString());
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
					System.out.println(this.loadToMinionMap.toString());
					System.out.println("REMOVED");
				}
				this.loadStatus.put(entry.getKey(), -1);
				this.loadToMinionMap.get(-1).add(entry.getKey());
			} else {
				System.out.println("UPDATING MINION " +  entry.getKey() + " from " + this.loadStatus.get(entry.getKey()) +  " to 1 LOAD STATUS");
				if (this.loadStatus.get(entry.getKey()) != 1) {
					System.out.println("WASNT THIS BEFORE!");
					System.out.println(this.loadToMinionMap.toString());
					this.loadToMinionMap.get(this.loadStatus.get(entry.getKey())).remove(entry.getKey());
					System.out.println(this.loadToMinionMap.toString());
					System.out.println("REMOVED");
				}
				
				this.loadStatus.put(entry.getKey(), 1);
				this.loadToMinionMap.get(1).add(entry.getKey());
			}
		}
		
		System.out.println(this.minionMemoryMap.toString());
		System.out.println(this.minionDistMap.toString());
		System.out.println(this.loadToMinionMap.toString());
		System.out.println(this.loadStatus.toString());
	}

	private void updateGlobal() {
		this.globalMemory = 0;
		for (Entry<String, Double> entry : this.minionMemoryMap.entrySet()) {
			this.globalMemory += entry.getValue();
		}
		System.out.println("Global is now: " + this.globalMemory);
	}

}
