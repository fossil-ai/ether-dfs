package utils;

import java.io.Serializable;

public class WriteStamp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long txid;
	private long timestamp;
	private MinionLocation minion_location;

	
	public WriteStamp(long txid, long timestamp, MinionLocation location) {
		this.txid = txid;
		this.timestamp = timestamp;
		this.minion_location = location;
	}


	public long getTransactionId() {
		return this.txid;
	}


	public long getTimestamp() {
		return this.timestamp;
	}


	public MinionLocation getMinionLocation() {
		return this.minion_location;
	}

}
