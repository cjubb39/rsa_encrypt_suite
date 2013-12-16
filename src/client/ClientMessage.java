package client;

import java.util.ArrayList;

import shared.ServerMessage;
import shared.User;

public class ClientMessage {

	private final User sender;
	private ArrayList<User> recipients;
	private String message = "";
	
	public ClientMessage(User sender, ArrayList<User> recipients, String message){
		this.sender = sender;
		this.recipients = recipients;
		this.message = message;
	}
	
	public ClientMessage(User sender) {
		this.sender = sender;
		this.recipients = new ArrayList<User>();
	}
	
	public ClientMessage(User sender, User recipient, String message){
		this.sender = sender;
		
		this.recipients = new ArrayList<User>();
		this.recipients.add(recipient);
		
		this.message = message;
	}
	
	public ClientMessage[] expandArray(){
		ClientMessage[] toRet = new ClientMessage[this.recipients.size()];
		
		for(int i = 0; i < toRet.length; i++){
			toRet[i] = new ClientMessage(this.sender, this.recipients.get(i), this.message);
		}
		
		return toRet;
	}
	
	public shared.ServerMessage toServerMessage(){
		return new ServerMessage(this.sender, this.recipients.get(0), this.message.getBytes());
	}
	
	public shared.ServerMessage[] toServerMessages(){
		ServerMessage[] toRet = new ServerMessage[this.recipients.size()];
			
		for(int i = 0; i < toRet.length; i++)
			toRet[i] = new ServerMessage(this.sender, this.recipients.get(i), this.message.getBytes());
		
		return toRet;	
	}
	
	public User getSender(){
		return this.sender;
	}
	
	public ArrayList<User> getRecipients(){
		return this.recipients;
	}
	
	public ArrayList<User> addRecipient(User in){
		this.recipients.add(in);
		return this.recipients;
	}
	
	public void clearRecipients(){
		this.recipients.clear();
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}

}
