package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import shared.ServerMessage;
import shared.User;

public class InboxMessage implements java.io.Serializable{

	private static final long serialVersionUID = -8850782539102043049L;
	public final String sender;
	public final String recipient;
	public final Date date;
	public final String message;
	private transient final AddressBook addressBook;
		
	public InboxMessage(ServerMessage sm, AddressBook addressBook){
		this.addressBook = addressBook;;
		
		this.date = sm.getDate();
		this.sender = this.lookupUser(sm.getSender());
		this.recipient = this.lookupUser(sm.getRecipient());
		this.message = new String(sm.getMessage());
	}
	
	public String lookupUser(long id){
		Iterator<User> it = this.addressBook.getData().listIterator();
		
		User temp;
		while(it.hasNext()){
			if((temp = it.next()).getID() == id){
				return temp.toString();
			}
		}
		
		return String.valueOf(id);
	}
}
