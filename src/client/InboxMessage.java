package client;

import java.util.Date;

import shared.ServerMessage;
import shared.TableData;
import shared.User;

public class InboxMessage implements TableData, java.io.Serializable{

	private static final long serialVersionUID = -8850782539102043049L;
	public transient boolean delete;
	public final String sender;
	@SuppressWarnings("unused")
	private final String recipient;
	public final Date date;
	public final String message;
		
	public InboxMessage(ServerMessage sm, AddressBook addressBook){
		this.date = sm.getDate();
		
		User tmp = addressBook.lookupByID(sm.getSender());
		this.sender = (tmp == null) ? String.valueOf(sm.getSender()) : tmp.toString();
		
		tmp = addressBook.lookupByID(sm.getRecipient());
		this.recipient = (tmp == null) ? String.valueOf(sm.getRecipient()) : tmp.toString();
		
		this.message = new String(sm.getMessage());
		this.delete = false;
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
