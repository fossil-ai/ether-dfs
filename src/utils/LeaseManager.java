package utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeaseManager {
	
	ConcurrentHashMap<String, Lease> leases;
	ScheduledExecutorService scheduler;
	
	public LeaseManager() {
		this.leases = new ConcurrentHashMap<String, Lease>();
		this.scheduler = Executors.newScheduledThreadPool(100);
	}
	
	public boolean grantLease(String ClientID, String globalFileName){
		if(this.leaseExist(globalFileName)){
			if(this.getLeaseHolder(globalFileName).equalsIgnoreCase(ClientID)){
				return true;
			}
			else {
				return false;
			}
		}
		else {
			Lease lease = new Lease(globalFileName, ClientID, 10);
			this.leases.put(globalFileName, lease);
			this.scheduler.schedule(this.leases.get(globalFileName), 5, TimeUnit.SECONDS);
			return true;
		}
	}
	
	public String getLeaseHolder(String globalFileName){
		return this.leases.get(globalFileName).getHolder();
	}
	
	private boolean leaseExist(String filename){
		if(this.leases.containsKey(filename)) {
			Lease lease = this.leases.get(filename);
			if(lease.isExpired()){
				this.removeLease(filename);
				return false;
			}
			return this.leases.containsKey(filename);
		}
		else {
			return false;
		}
	}
	
	private void removeLease(String filename){
		this.leases.remove(filename);
	}
	
	
}
