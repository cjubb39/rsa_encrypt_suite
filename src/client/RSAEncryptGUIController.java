package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import rsaEncrypt.KeyFile;
import shared.ServerMessage;
import shared.User;

public class RSAEncryptGUIController implements ActionListener, WindowListener {

	private final RSAEncryptGUI gui;
	private AddressBook contactManager;
	private ServerList serverManager;
	//private JList addRecipList;
	
	public static final JFileChooser fc = new JFileChooser("./");
	
	public RSAEncryptGUIController(RSAEncryptGUI gui){
		this.gui = gui;
		
		this.contactManager = new AddressBook(new ArrayList<User>());
		this.serverManager = new ServerList(new ArrayList<ServerProfile>());
		this.updateManagers();
	}
	
	public void updateManagers(){
		if(this.gui.getActiveProfile().getAddressBook() != this.contactManager.getData()){
			this.contactManager = new AddressBook(this.gui.getActiveProfile().getAddressBook());
			this.contactManager.tableModel.updateFields();
		}
		
		if(this.gui.getActiveProfile().getServers() != this.serverManager.getData()){
			this.serverManager = new ServerList(this.gui.getActiveProfile().getServers());
			this.serverManager.tableModel.updateFields();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTextArea messageArea = this.gui.getMessageArea();
		
		if (arg0.getSource().equals(this.gui.getClearButton())){
			this.clearMessage(messageArea);
		} else if (arg0.getSource().equals(this.gui.getSendButton())){
			this.prepareAndSendMessage(messageArea);
		} else if (arg0.getSource().equals(this.gui.getExitButton())){
			this.exitSequence();
		} else if (arg0.getSource().equals(this.gui.getAddContact())){
			this.contactManager.addOne();
		} else if (arg0.getSource().equals(this.gui.getViewContacts())){
			this.contactManager.viewAll();
		} else if (arg0.getSource().equals(this.gui.getAddServer())){
			this.serverManager.addOne();
			this.updateServerInfo();
		} else if (arg0.getSource().equals(this.gui.getViewServers())){
			this.serverManager.viewAll();
		} else if (arg0.getSource().equals(this.gui.getExportPublicKey())){
			UserProfile active = this.gui.getActiveProfile();
			this.exportKey(active.getKp().getPub(), 
					active.getMe().getFirstName() + "_" + active.getMe().getLastName() + "_public");
		} else if (arg0.getSource().equals(this.gui.getExportPrivateKey())){
			// we want to make sure user knows what they're doing
			int option = JOptionPane.showConfirmDialog(this.gui.getMainGUI(), 
					"Attempting to Export Private Key.  This is potentially very dangerous.  Are you sure you want to continue?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			
			if (option == JOptionPane.YES_OPTION){
				UserProfile active = this.gui.getActiveProfile();
				this.exportKey(active.getKp().getPriv(), 
						active.getMe().getFirstName() + "_" + active.getMe().getLastName() + "_private");
			}
		} else if (arg0.getSource().equals(this.gui.getAddRecipientButton())){
			this.addRecipientToCurrentMessage();
		}
		/***********************************************/
		else if(arg0.getSource().equals(this.gui.getReceiveMessagesButton())){
			this.receiveMessages();
		}
		/************************************************/

	}
	
	private void addRecipientToCurrentMessage(){
		new AddRecipientsGUI(this.contactManager, this.gui.getCurrentMessage(), this.gui.getTxtRecipient());
	}
	
	private void updateServerInfo(){
System.out.println("update server");
		this.gui.setActiveServer(this.gui.getActiveProfile().getServers().get(0));
	}
	
	private void updateAddressBookInfo(){
		
	}
	
	private void clearMessage(JTextArea message){
		int n = JOptionPane.showConfirmDialog(null, "Do you really want to clear?", 
				"RSA Encryption Suite", JOptionPane.OK_CANCEL_OPTION);
		if (n == JOptionPane.OK_OPTION){
			this.resetCompose();
		}
	}
	
	private boolean prepareAndSendMessage(JTextArea message){
		// get proper message
		this.gui.getCurrentMessage().setMessage(this.gui.getMessageArea().getText());
		ServerMessage[] toSend = this.gui.getCurrentMessage().toServerMessages();
		
		// Add user. FOR TEST PURPOSES ONLY //TODO
		try {
			ServerHandler.addUser(this.gui.getActiveProfile(), this.gui.getActiveServer());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean success = true;
		for (ServerMessage sm : toSend){
			if(!this.sendMessage(sm, this.gui.getActiveProfile(), this.gui.getActiveServer())){
				success = false;
			}
		}
		this.resetCompose();
		
		return success;
	}
	
	private boolean sendMessage(ServerMessage toSend, UserProfile sender, ServerProfile server){
		boolean returnStatus = false;
		try {
			returnStatus = ServerHandler.sendMessage(sender, toSend, server);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return returnStatus;
	}
	
	public boolean receiveMessages(){
		ServerMessage[] ret = null;
		try{
			ret = ServerHandler.receiveMessages(this.gui.getActiveProfile(), this.gui.getActiveServer());
		} catch (IOException e){
			return false;
		}
		
		if (ret == null) return false;
		
		for (ServerMessage sm : ret){
			this.gui.getInboxController().addOne(new InboxMessage(sm, this.contactManager));
		}
		
		return true;
	}
	
	private void resetCompose(){
		this.gui.resetCurrentMessage();
		this.gui.getMessageArea().setText("");
	}
	
	private void exportKey(KeyFile kf, String fileName){
		RSAEncryptGUIController.fc.setSelectedFile(new File(fileName));
		RSAEncryptGUIController.fc.showSaveDialog(this.gui.getMainGUI());
		try {
			shared.Utilities.serializeToFile(kf, RSAEncryptGUIController.fc.getSelectedFile());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this.gui.getMainGUI(), "Failed to Save Key!");
			e.printStackTrace();
		}
		
		RSAEncryptGUIController.fc.setSelectedFile(new File(""));
	}
	
	private void exitSequence(){
		/*String message, header = "Exit Confirmation";
		message = "Do you want to save before exiting?";

		int n = JOptionPane.showConfirmDialog(null, message, header,
				JOptionPane.YES_NO_CANCEL_OPTION);

		// if yes, do exit procedure. Else, return to normal state.
		switch (n) {
			case JOptionPane.CANCEL_OPTION:
				return;

			case JOptionPane.YES_OPTION:
				this.gui.saveProfile();

			case JOptionPane.NO_OPTION:
				System.exit(0);
				break;
		}*/
		
		this.gui.saveProfile();
		System.exit(1);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// essentially "presses the exit button"
		this.actionPerformed(new ActionEvent(this.gui.getExitButton(), ActionEvent.ACTION_PERFORMED, null));
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
