package client.table.gui;

import java.util.ArrayList;

import javax.swing.JLabel;

import shared.User;
import client.message.ClientMessage;
import client.table.AddressBook;

/**
 * Instantiation of SelectPropertyGUI<AddressBook> used to select contacts to be added as
 * recipients
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class AddRecipientsGUI extends SelectPropertyGUI<AddressBook> {

	private ClientMessage message;
	private ArrayList<User> userData;
	private JLabel text;

	/**
	 * Constructor
	 * 
	 * @param addressBook
	 *          AddressBook of contacts (choices)
	 * @param message
	 *          ClientMessage to add recipients to
	 * @param text
	 *          Text Label used to show GUI results
	 * @param header
	 *          Header bar text
	 */
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message, JLabel text, String header){
		super(addressBook, header);
		this.message = message;
		this.userData = this.getData().getData();
		this.text = (text == null) ? new JLabel() : text;
	}

	/**
	 * Constructor. Uses default header text: "Selected Recipients to Add".
	 * 
	 * @param addressBook
	 *          AddressBook of contacts (choices)
	 * @param message
	 *          ClientMessage to add recipients to
	 * @param text
	 *          Text Label used to show GUI results
	 */
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message, JLabel text){
		this(addressBook, message, text, "Select Recipients to Add");
	}

	/**
	 * Constructor. Uses default header text: "Selected Recipients to Add" and empty text
	 * label
	 * 
	 * @param addressBook
	 *          AddressBook of contacts (choices)
	 * @param message
	 *          ClientMessage to add recipients to
	 */
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message){
		this(addressBook, message, null);
	}

	/**
	 * Updates given text label with chosen recipients toString representation
	 * 
	 * @see client.table.gui.SelectPropertyGUI#actionOnSet()
	 */
	public void actionOnSet(){
		int[] indeces = this.getDataList().getSelectedIndices();

		for (int i : indeces) {
			this.message.addRecipient(userData.get(i));
			this.text.setText(this.text.getText() + this.userData.get(i).toString() + ", ");
		}
	}
}
