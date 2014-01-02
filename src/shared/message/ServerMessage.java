package shared.message;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import rsaEncrypt.key.KeyFile;
import rsaEncrypt.message.RSAMessage;
import shared.User;

/**
 * Extension of RSAMessage with fields necessary for the server to hold it
 * 
 * @author Chae Jubb
 * @version 2.0
 * 
 */
public class ServerMessage extends RSAMessage implements Serializable {

	private static final long serialVersionUID = 2592083189680331232L;

	private final long sender;
	private final long recipient;
	private final Date date;

	/**
	 * Constructor.
	 * 
	 * @param sender
	 *          UserID of sender
	 * @param recipient
	 *          UserID of recipient
	 * @param message
	 *          Message as byte array. May be encrypted or not.
	 * @param date
	 *          Date of message
	 * @param align
	 *          Messages aligns to read-chunk if true; does not necessarily otherwise
	 */
	public ServerMessage(long sender, long recipient, byte[] message, Date date, boolean align){
		super(message, align);

		this.sender = sender;
		this.recipient = recipient;
		this.date = date;
	}

	/**
	 * Constructor. Uses default alignment
	 * 
	 * @param sender
	 *          UserID of sender
	 * @param recipient
	 *          UserID of recipient
	 * @param message
	 *          Message as byte array. May be encrypted or not.
	 * @param date
	 *          Date of message
	 */
	public ServerMessage(long sender, long recipient, byte[] message, Date date){
		super(message);
		this.sender = sender;
		this.recipient = recipient;
		this.date = date;
	}

	/**
	 * Constructor. Uses current date and time.
	 * 
	 * @param sender
	 *          UserID of sender
	 * @param recipient
	 *          UserID of recipient
	 * @param message
	 *          Message as byte array. May be encrypted or not.
	 * @param align
	 *          Messages aligns to read-chunk if true; does not necessarily otherwise
	 */
	public ServerMessage(long sender, long recipient, byte[] message, boolean align){
		this(sender, recipient, message, new Date(), align);
	}

	/**
	 * Constructor. Uses current date and time and default alignment.
	 * 
	 * @param sender
	 *          UserID of sender
	 * @param recipient
	 *          UserID of recipient
	 * @param message
	 *          Message as byte array. May be encrypted or not.
	 */
	public ServerMessage(long sender, long recipient, byte[] message){
		this(sender, recipient, message, new Date());
	}

	/**
	 * Constructor using User. Uses current time and date and default alignment
	 * 
	 * @param sender
	 *          User of sender
	 * @param recipient
	 *          User of recipient
	 * @param message
	 *          Message as byte array. May be encrypted or not.
	 */
	public ServerMessage(User sender, User recipient, byte[] message){
		this(sender.getID(), recipient.getID(), message);
	}

	/**
	 * @param key
	 *          Keyfile to encrypt with
	 * @return Encrypted message. Non-mutable method: does not change this.
	 * @see rsaEncrypt.message.RSAMessage#encryptMessage(rsaEncrypt.key.KeyFile)
	 */
	public ServerMessage encryptMessage(KeyFile key){
		return new ServerMessage(this.getSender(), this.getRecipient(), super.encryptMessage(key)
				.getMessage(), this.getDate());
	}

	/**
	 * @param key
	 *          Keyfile to decrypt with
	 * @return Decrypted message. Non-mutable method: does not change this.
	 * @see rsaEncrypt.message.RSAMessage#decryptMessage(rsaEncrypt.key.KeyFile)
	 */
	public ServerMessage decryptMessage(KeyFile key){
		return new ServerMessage(this.getSender(), this.getRecipient(), super.decryptMessage(key)
				.getMessage(), this.getDate());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return (new BigInteger(this.getMessage())).xor(BigInteger.valueOf(this.sender))
				.xor(BigInteger.valueOf(this.recipient)).xor(BigInteger.valueOf(this.date.hashCode()))
				.intValue();
	}

	/**
	 * @return the sender
	 */
	public long getSender(){
		return this.sender;
	}

	/**
	 * @return the recipient
	 */
	public long getRecipient(){
		return this.recipient;
	}

	/**
	 * @return the date
	 */
	public Date getDate(){
		return this.date;
	}
}
