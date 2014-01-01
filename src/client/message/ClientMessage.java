package client.message;

import java.util.ArrayList;

import shared.message.ServerMessage;
import shared.User;

/**
 * Holds information necessary to convert compose message space to server message
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class ClientMessage {

	private final User sender;
	private ArrayList<User> recipients;
	private String message = "";

	/**
	 * Constructor
	 * 
	 * @param sender
	 *          User sending message
	 * @param recipients
	 *          List of users to receive the message
	 * @param message
	 *          Message contents, as a String
	 */
	public ClientMessage(User sender, ArrayList<User> recipients, String message){
		this.sender = sender;
		this.recipients = recipients;
		this.message = message;
	}

	/**
	 * Constructor. Creates blank message and blank list of recipients
	 * 
	 * @param sender
	 *          User sending message
	 */
	public ClientMessage(User sender){
		this.sender = sender;
		this.recipients = new ArrayList<User>();
	}

	/**
	 * Constructor. Creates message to one user.
	 * 
	 * @param sender
	 *          User sending message
	 * @param recipient
	 *          Single recipient of message
	 * @param message
	 *          Message contents
	 */
	public ClientMessage(User sender, User recipient, String message){
		this.sender = sender;

		this.recipients = new ArrayList<User>();
		this.recipients.add(recipient);

		this.message = message;
	}

	/**
	 * Creates an array of messages--one for each recipient--from message containing list of
	 * recipients
	 * 
	 * @return Array of messages
	 */
	public ClientMessage[] expandArray(){
		ClientMessage[] toRet = new ClientMessage[this.recipients.size()];

		for (int i = 0; i < toRet.length; i++) {
			toRet[i] = new ClientMessage(this.sender, this.recipients.get(i), this.message);
		}

		return toRet;
	}

	/**
	 * Converts this to ServerMessage
	 * 
	 * @return ServerMessage form of this
	 */
	public ServerMessage toServerMessage(){
		return new ServerMessage(this.sender, this.recipients.get(0), this.message.getBytes());
	}

	/**
	 * Converts this to array of ServerMessage (useful with multiple recipients)
	 * 
	 * @return Array of ServerMessages that can be sent to a server
	 */
	public ServerMessage[] toServerMessages(){
		ServerMessage[] toRet = new ServerMessage[this.recipients.size()];

		for (int i = 0; i < toRet.length; i++)
			toRet[i] = new ServerMessage(this.sender, this.recipients.get(i), this.message.getBytes());

		return toRet;
	}

	/**
	 * @return Sender of message
	 */
	public User getSender(){
		return this.sender;
	}

	/**
	 * @return Recipients of message
	 */
	public ArrayList<User> getRecipients(){
		return this.recipients;
	}

	/**
	 * Adds specified User to list of recipients
	 * 
	 * @param in
	 *          User to add as recipient
	 * @return List of all recipients
	 */
	public ArrayList<User> addRecipient(User in){
		this.recipients.add(in);
		return this.recipients;
	}

	/**
	 * Removes all users from list of recipients
	 */
	public void clearRecipients(){
		this.recipients.clear();
	}

	/**
	 * @return Message
	 */
	public String getMessage(){
		return this.message;
	}

	/**
	 * Sets message as specified
	 * 
	 * @param message
	 *          New message to hold
	 */
	public void setMessage(String message){
		this.message = message;
	}

}
