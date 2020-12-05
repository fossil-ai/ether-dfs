package utils;

import java.io.Serializable;

public class Lease implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5156964742420240496L;
	private String globalFileName;
	private String clientID;
	private int leaseTerm;
	private boolean isExpired;

	public Lease(String globalFileName, String clientID, int leaseTerm) {
		this.globalFileName = globalFileName;
		this.clientID = clientID;
		this.leaseTerm = leaseTerm;
		this.isExpired = false;
	}

	public String getHolder() {
		return this.clientID;
	}
	
	public boolean isExpired(){
		return this.isExpired;
	}

	private void live() {
		try {
			this.isExpired = false;
			Thread.sleep(this.leaseTerm * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		System.out.println("Starting Client " + this.clientID + "'s lease on " + this.globalFileName + " for a term of "
				+ this.leaseTerm + " seconds.");
		this.live();
		System.out.println("Client " + this.clientID + " lease on " + this.globalFileName + " is over.");	
		this.isExpired = true;
	}

}
