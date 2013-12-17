package client;

import java.util.ArrayList;

import javax.swing.JLabel;

import shared.User;

public class AddRecipientsGUI extends SelectPropertyGUI<AddressBook>{
	
	private ClientMessage message;
	private ArrayList<User> userData;
	private JLabel text;
	
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message, JLabel text, String header) {
		super(addressBook, header);
		this.message = message;
		this.userData = this.getData().getData();
		this.text = (text == null) ? new JLabel() : text;
	}
	
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message, JLabel text){
		this(addressBook, message, text, "Select Recipients to Add");
	}
	
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message) {
		this(addressBook, message, null);
	}
	
	public void actionOnSet(){
		int[] indeces = this.getDataList().getSelectedIndices();
		
		for (int i : indeces){
			this.message.addRecipient(userData.get(i));
			this.text.setText(this.text.getText() + this.userData.get(i).toString() + ", ");
		}
	}
}
