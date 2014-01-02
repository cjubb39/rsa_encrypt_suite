package client.message;

import java.util.Date;

import client.table.AddressBook;

import shared.message.ServerMessage;
import shared.TableData;
import shared.User;

/**
 * Holds message as shown in Inbox
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class InboxMessage implements TableData, java.io.Serializable {

	private static final long serialVersionUID = -8850782539102043049L;
	public transient boolean delete;
	public final String sender;
	private final String recipient;
	public final Date date;
	public final String message;

	/**
	 * Constructor
	 * 
	 * @param sm
	 *          Server message to create InboxMessage from
	 * @param addressBook
	 *          AddressBook used to look up users by ID
	 */
	public InboxMessage(ServerMessage sm, AddressBook addressBook){
		this.date = sm.getDate();

		User tmp = addressBook.lookupByID(sm.getSender());
		this.sender = (tmp == null) ? String.valueOf(sm.getSender()) : tmp.toString();

		tmp = addressBook.lookupByID(sm.getRecipient());
		this.recipient = (tmp == null) ? String.valueOf(sm.getRecipient()) : tmp.toString();

		this.message = new String(sm.getMessage());
		this.delete = false;
	}

	/**
	 * Constructor the rechecks user IDs for name from address book
	 * 
	 * @param im
	 *          Message to recheck
	 * @param addressBook
	 *          AddressBook to check message with
	 */
	public InboxMessage(InboxMessage im, AddressBook addressBook){
		// update non-user fields
		this.date = im.date;
		this.message = im.message;

		// update user fields (if necessary/able)
		if (im.sender.matches("\\d+")) {
			User tmp = addressBook.lookupByID(Long.parseLong(im.sender));
			this.sender = (tmp == null) ? String.valueOf(im.sender) : tmp.toString();
		} else {
			this.sender = im.sender;
		}

		if (im.recipient.matches("\\d+")) {
			User tmp = addressBook.lookupByID(Long.parseLong(im.recipient));
			this.recipient = (tmp == null) ? String.valueOf(im.recipient) : tmp.toString();
		} else {
			this.recipient = im.recipient;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see shared.TableData#getDelete()
	 */
	@Override
	public boolean getDelete(){
		return this.delete;
	}

	/*
	 * (non-Javadoc)
	 * @see shared.TableData#setDelete(boolean)
	 */
	@Override
	public void setDelete(boolean in){
		this.delete = in;
	}
}
