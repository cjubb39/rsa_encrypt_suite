package client;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

import rsaEncrypt.KeyFile;
import shared.User;

public class AddressBook extends ListManager<User>{

	private JButton addOpenButton;
	private File keyFileLocation;
	private transient final static JFileChooser fc = new JFileChooser(new File("./"));
	
	public AddressBook(ArrayList<User> data) {
		super(data);
	}

	@Override
	public ArrayList<User> addOne() {
		JTextField firstName = new JTextField(), lastName = new JTextField();
		this.addOpenButton = new JButton("Open Public Key File");
		this.addOpenButton.addActionListener(this);
		
		Object[] message = { "First Name: ", firstName, "Last Name: ", lastName, "Select Key File:", this.addOpenButton};
	
		boolean finished = false;
		while (!finished) {
			this.keyFileLocation = null;
			int option = -1;
			do {
				option = JOptionPane.showConfirmDialog(this.getGUI(), message,
						"Add Contact", JOptionPane.OK_CANCEL_OPTION);
			} while (this.keyFileLocation == null && option == -1);
			
			if (option == JOptionPane.OK_OPTION) {
				if (firstName.getText().equals("") || lastName.getText().equals("")){
					JOptionPane.showMessageDialog(this.getGUI(),"All fields must be filled out!");
					continue;
				}
				// serialize key
				try {
					KeyFile kf = (KeyFile) shared.Utilities.deserializeFromFile(keyFileLocation);
					this.getData().add(new User(firstName.getText(), lastName.getText(), kf));
					finished = true;
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this.getGUI(), "Error Reading File");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this.getGUI(),"Invalid Key File");
				}
			} else {
				finished = true;
			}
		}
		this.keyFileLocation = null; //clear out
		
		return this.getData();
	}
	
	public ArrayList<User> addOne(User in){
		this.getData().add(in);		
		return this.getData();
	}
	
	public File getKeyFile(){
		int returnVal = AddressBook.fc.showOpenDialog(this.getGUI());
		
		if (returnVal == JFileChooser.APPROVE_OPTION){
			this.keyFileLocation = fc.getSelectedFile();
		}
		
		return this.keyFileLocation;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource().equals(this.getViewExitButton())){
			this.getGUI().dispose();
		} else if (e.getSource().equals(this.getViewAddButton())){
			this.addOne();
			this.resetTable();
		} else if (e.getSource().equals(this.addOpenButton)){
			this.getKeyFile();
		} else if (e.getSource().equals(this.getDeleteButton())){
			this.deleteSelected();
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {}
}
