package utils;

import java.util.ArrayList;
import java.util.List;

import links.MasterMinionLink;

public class MinionManager {
	
	private List<MinionLocation> minionLocations;
	private List<MasterMinionLink> minionMasterInvocation; 
	
	public MinionManager(){
		this.minionLocations = new ArrayList<MinionLocation>();
		this.minionMasterInvocation = new ArrayList<MasterMinionLink>();
	}
	
	public void addMinion(MinionLocation minionLocation) {
		this.minionLocations.add(minionLocation);
		//this.minionMasterInvocation.add((MasterMinionLink) stub);
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

	public List<MasterMinionLink> getMinionMasterInvocation() {
		return minionMasterInvocation;
	}

	public void setMinionMasterInvocation(List<MasterMinionLink> minionMasterInvocation) {
		this.minionMasterInvocation = minionMasterInvocation;
	}

}
