package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import shared.User;

public class AddRecipientsGUI implements ActionListener{
	
	private String[] strData;
	private ArrayList<User> userData;
	private ClientMessage message;
	private AddressBook addressBook;
	private JTextField text;
	
	private JList addRecipList;
	private JDialog addRecip;
	private JButton cancelButton;
	private JButton setButton;
	
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message, JTextField text) {
		this.addressBook = addressBook;
		this.message = message;
		this.strData = this.addressBook.getDataStringArray();
		this.userData = this.addressBook.getData();
		this.text = (text == null) ? new JTextField() : text;
		
		this.initGUI();
	}
	
	public AddRecipientsGUI(AddressBook addressBook, ClientMessage message) {
		this(addressBook, message, null);
	}
	
	public void initGUI(){
		this.addRecip = new JDialog();
		this.addRecip.setSize(new Dimension(350,250));
		this.addRecip.setLayout(new BorderLayout());
		
		// set up list
		this.addRecipList = new JList(this.strData);
		this.addRecipList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.addRecipList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//this.addRecipList.setVisibleRowCount(-1);
		this.addRecipList.setFixedCellWidth(100);
		this.addRecipList.setFixedCellHeight(25);
		JScrollPane scrollPane = new JScrollPane(this.addRecipList);
		
		//get buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new BorderLayout());
		
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);
		
		this.setButton = new JButton("Select");
		this.setButton.addActionListener(this);
		
		buttons.add(this.setButton, BorderLayout.WEST);
		buttons.add(this.cancelButton, BorderLayout.EAST);
		
		this.addRecip.add(buttons, BorderLayout.SOUTH);
		this.addRecip.add(scrollPane, BorderLayout.CENTER);
		this.addRecip.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(this.cancelButton)){
			this.addRecip.dispose();
		} else if (arg0.getSource().equals(this.setButton)){
			int[] indeces = this.addRecipList.getSelectedIndices();
			
			for (int i : indeces){
				this.message.addRecipient(userData.get(i));
				this.text.setText(this.text.getText() + this.userData.get(i).toString() + ", ");
			}
			
			this.addRecip.dispose();
		}
	}

}
