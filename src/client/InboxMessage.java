package client;

import java.util.Date;
import java.util.Iterator;

import shared.ServerMessage;
import shared.TableData;
import shared.User;

public class InboxMessage implements TableData, java.io.Serializable{

	private static final long serialVersionUID = -8850782539102043049L;
	public transient boolean delete;
	public final String sender;
	private final String recipient;
	public final Date date;
	public final String message;
	private transient final AddressBook addressBook;
		
	public InboxMessage(ServerMessage sm, AddressBook addressBook){
		this.addressBook = addressBook;;
		
		this.date = sm.getDate();
		this.sender = this.lookupUser(sm.getSender());
		this.recipient = this.lookupUser(sm.getRecipient());
		this.message = new String(sm.getMessage());
		this.delete = false;
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

	@Override
	public boolean getDelete() {
		return this.delete;
	}

	@Override
	public void setDelete(boolean in) {
		this.delete = in;
	}
}
