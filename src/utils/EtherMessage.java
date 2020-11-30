package utils;

import java.io.Serializable;

public class EtherMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2732241293052496211L;
	private String source;
	private String destination;
	private long txid;
	private long timestamp;

	private String message;

	public EtherMessage() {

	}

	public String getDestination() {
		return destination;
	}

	public EtherMessage setDestination(String destination) {
		this.destination = destination;
		return this;
	}

	public String getSource() {
		return source;
	}

	public EtherMessage setSource(String source) {
		this.source = source;
		return this;
	}

	public long getTxid() {
		return txid;
	}

	public EtherMessage setTxid(long txid) {
		this.txid = txid;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public EtherMessage setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public EtherMessage setMessage(String message) {
		this.message = message;
		return this;
	}

}
