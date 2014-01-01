package client.table;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import rsaEncrypt.key.KeyFile;
import shared.User;

/**
 * Instantiation of ListManager<User> used to manage a user's contacts
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class AddressBook extends ListManager<User> implements Serializable {

	private static final long serialVersionUID = 117178744478130464L;
	private transient JButton addOpenButton;
	private transient File keyFileLocation;
	private transient final static JFileChooser fc = new JFileChooser(new File("./"));

	/**
	 * Constructor. Same as super(data);
	 * 
	 * @param data
	 *          List of contacts to manage
	 */
	public AddressBook(ArrayList<User> data){
		super(data);
	}

	/**
	 * Constructor. Same as super();
	 */
	public AddressBook(){
		super();
	}

	/**
	 * Adds user as specified in super.addOne(). Checks for valid keyfile when loading
	 * 
	 * @see client.table.ListManager#addOne()
	 */
	@Override
	public ArrayList<User> addOne(){
		JTextField firstName = new JTextField(), lastName = new JTextField();
		this.addOpenButton = new JButton("Open Public Key File");
		this.addOpenButton.addActionListener(this);

		Object[] message = {"First Name: ", firstName, "Last Name: ", lastName, "Select Key File:",
				this.addOpenButton};

		boolean finished = false;
		while (!finished) {
			this.keyFileLocation = null;
			int option = -1;
			do {
				option = JOptionPane.showConfirmDialog(this.getGUI(), message, "Add Contact",
						JOptionPane.OK_CANCEL_OPTION);
			} while (this.keyFileLocation == null && option == -1);

			if (option == JOptionPane.OK_OPTION) {
				if (firstName.getText().equals("") || lastName.getText().equals("")) {
					JOptionPane.showMessageDialog(this.getGUI(), "All fields must be filled out!");
					continue;
				}
				// deserialize key and make sure valid keyfile
				try {
					rsaEncrypt.key.KeyFile kf = (KeyFile) shared.Utilities
							.deserializeFromFile(keyFileLocation);
					this.getData().add(new User(firstName.getText(), lastName.getText(), kf));
					finished = true;
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this.getGUI(), "Error Reading File");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this.getGUI(), "Invalid Key File");
				}
			} else {
				finished = true;
			}
		}
		this.keyFileLocation = null; // clear out

		return this.getData();
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#addOne(shared.TableData)
	 */
	public ArrayList<User> addOne(User in){
		this.getData().add(in);
		return this.getData();
	}

	/**
	 * Get KeyFile from user via FileChooser
	 * 
	 * @return File chosen
	 */
	public File getKeyFile(){
		int returnVal = AddressBook.fc.showOpenDialog(this.getGUI());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.keyFileLocation = fc.getSelectedFile();
		}

		return this.keyFileLocation;
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e){
		if (e.getSource().equals(this.getViewExitButton())) {
			this.getGUI().dispose();
		} else if (e.getSource().equals(this.getViewAddButton())) {
			this.addOne();
			this.resetTable();
		} else if (e.getSource().equals(this.addOpenButton)) {
			this.getKeyFile();
		} else if (e.getSource().equals(this.getDeleteButton())) {
			this.deleteSelected();
		}
	}

	/**
	 * Lookup User by given ID
	 * 
	 * @param id
	 *          ID to search with
	 * @return First user with ID given
	 */
	public User lookupByID(long id){
		Iterator<User> it = this.getData().listIterator();
		while (it.hasNext()) {
			User temp;
			if ((temp = it.next()).getID() == id) {
				return temp;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0){}
}
