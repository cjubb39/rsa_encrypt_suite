package shared;

import java.util.Date;
import java.io.Serializable;

public class ServerMessage implements Serializable{

	private static final long serialVersionUID = 2592083189680331232L;
	private final byte[] message;
	private final long sender;
	private final long recipient;
	private final Date date;
	
	public static final int MAX_SIZE = 1024 * 1024 * 16; //16 MB
	
	public ServerMessage(long sender, long recipient, byte[] message, Date date){
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		this.date = date;
	}
	
	public ServerMessage(long sender, long recipient, byte[] message){
		this(sender, recipient, message, new Date());
	}
	
	public ServerMessage(User sender, User recipient, byte[] message){
		this(sender.getID(), recipient.getID(), message);
	}

	/**
	 * @return the message
	 */
	public byte[] getMessage() {
		return this.message;
	}

	/**
	 * @return the sender
	 */
	public long getSender() {
		return this.sender;
	}

	/**
	 * @return the recipient
	 */
	public long getRecipient() {
		return this.recipient;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}
	
}
