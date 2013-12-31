package shared;

import java.util.Date;
import java.io.Serializable;

import rsaEncrypt.KeyFile;

public class ServerMessage extends RSAMessage implements Serializable{

	private static final long serialVersionUID = 2592083189680331232L;
	
	private final long sender;
	private final long recipient;
	private final Date date;
	
	public ServerMessage(long sender, long recipient, byte[] message, Date date){
		super(message);
		this.sender = sender;
		this.recipient = recipient;
		this.date = date;
	}

	public ServerMessage(long sender, long recipient, byte[] message, Date date, boolean align){
		super(message, align);
		
		this.sender = sender;
		this.recipient = recipient;
		this.date = date;
	}

	public ServerMessage(long sender, long recipient, byte[] message, boolean align){
		this(sender, recipient, message, new Date(), align);
	}
	
	public ServerMessage(long sender, long recipient, byte[] message){
		this(sender, recipient, message, new Date());
	}
	
	public ServerMessage(User sender, User recipient, byte[] message){
		this(sender.getID(), recipient.getID(), message);
	}
	
	public ServerMessage encryptMessage(KeyFile key){
		return new ServerMessage(this.getSender(), this.getRecipient(), 
				super.encryptMessage(key).getMessage(), this.getDate());
	}
	
	public ServerMessage decryptMessage(KeyFile key){
		return new ServerMessage(this.getSender(), this.getRecipient(),
				super.decryptMessage(key).getMessage(), this.getDate());
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
