package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import rsaEncrypt.KeyFile;
import shared.CommBytes;
import shared.ServerMessage;
import shared.User;

public class RSAEncryptGUIController implements ActionListener, WindowListener, ComponentListener {

	private final RSAEncryptGUI gui;
	private AddressBook contactManager;
	private ServerList serverManager;
	
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
		}
		
		if(this.gui.getActiveProfile().getServers() != this.serverManager.getData()){
			this.serverManager = new ServerList(this.gui.getActiveProfile().getServers());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JTextArea messageArea = this.gui.getMessageArea();
		
		this.updateManagers();
		
		if (arg0.getSource().equals(this.gui.getClearButton())){
			this.clearMessage(messageArea);
		} else if (arg0.getSource().equals(this.gui.getSendButton())){
			(new Thread(){
				public void run(){
					startProgressBar("Sending Message");
					prepareAndSendMessage(messageArea);
					stopProgressBar();
				}
			}).start();
		} else if (arg0.getSource().equals(this.gui.getExitButton())){
			this.exitSequence();
		} else if (arg0.getSource().equals(this.gui.getAddContact())){
			this.contactManager.addOne();
			this.updateAddressBookInfo();
		} else if (arg0.getSource().equals(this.gui.getViewContacts())){
			this.contactManager.viewAll();
		} else if (arg0.getSource().equals(this.gui.getAddServer())){
			this.updateServerInfo(this.serverManager.addOne());
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
		else if(arg0.getSource().equals(this.gui.getReceiveMessagesButton())){
			(new Thread(){
				public void run(){
					startProgressBar("Checking Messages");
					receiveMessages();
					stopProgressBar();
				}
			}).start();
		} else if (arg0.getSource().equals(this.gui.getSetActiveServerButton())){
			this.setActiveServer();
		}

	}
	
	private void addRecipientToCurrentMessage(){
		new AddRecipientsGUI(this.contactManager, this.gui.getCurrentMessage(), this.gui.getTxtRecipient());
	}
	
	private void updateServerInfo(ArrayList<ServerProfile> sp){
		this.gui.setActiveServer(sp.get(sp.size() - 1));
	}
	
	private void updateAddressBookInfo(){
		
	}
	
	public void setActiveServer(){
		new SelectActiveServerGUI(new ServerList(this.gui.getActiveProfile().getServers()), this);
	}
	
	private void clearMessage(JTextArea message){
		int n = JOptionPane.showConfirmDialog(null, "Do you really want to clear?", 
				"RSA Encryption Suite", JOptionPane.OK_CANCEL_OPTION);
		if (n == JOptionPane.OK_OPTION){
			this.resetCompose();
		}
	}
	
	private boolean prepareAndSendMessage(JTextArea message){
		if (this.serverManager.getData().size() == 0){
			JOptionPane.showMessageDialog(this.gui.getMainGUI(), "Must Select Valid Active Server!", 
					"Invalid Server", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (this.gui.getCurrentMessage().getRecipients().size() == 0){
			return false;
		}
		
		// get proper message
		this.gui.getCurrentMessage().setMessage(this.gui.getMessageArea().getText());
		ServerMessage[] toSend = this.gui.getCurrentMessage().toServerMessages();
		
		this.sendMessage(toSend, this.gui.getActiveProfile(), this.gui.getActiveServer());
		
		this.resetCompose();
		
		return true;
	}
	
	private boolean sendMessage(ServerMessage[] toSend, UserProfile sender, ServerProfile server){
		// set up list of messages
		List<ServerMessage> messages = Arrays.asList(toSend);
		
		boolean returnStatus = false;
		try {
			returnStatus = ServerHandler.connectAndAct((byte) (CommBytes.addNewUser|CommBytes.sendMessage), 
					this.gui.getActiveServer(), this.gui.getActiveProfile(), messages, null);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return returnStatus;
	}
	

	public boolean receiveMessages(){
		List<ServerMessage> ret = new ArrayList<ServerMessage>();
		
		try{
			ServerHandler.connectAndAct((byte) (CommBytes.addNewUser|CommBytes.receiveMessage), 
					this.gui.getActiveServer(), this.gui.getActiveProfile(), null, ret);
		} catch (IOException e){
			return false;
		}
		
		if (ret.size() == 0) return false;
		
		for (ServerMessage sm : ret){
			this.gui.getInboxController().addOne(new InboxMessage(sm, this.contactManager));
		}
		
		return true;
	}
	
	private void resetCompose(){
		this.gui.resetCurrentMessage();
		this.gui.getMessageArea().setText("");
		this.gui.getTxtRecipient().setText("Recipient: ");
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
	
	public void exitSequence(){
		this.gui.exitSequence();
	}
	
	public void setActiveServer(ServerProfile serv){
		this.gui.setActiveServer(serv);
	}
	
	public boolean startProgressBar(String message){
		JProgressBar progress = this.gui.getProgressBar();
		if (progress.isVisible()){
			return false;
		} else {
			progress.setIndeterminate(true);
			progress.setString(message);
			progress.setVisible(true);
		 	return true;
		}
	}
	
	public boolean startProgressBar(){
		return this.startProgressBar("Working...");
	}
	
	public boolean stopProgressBar(){
		JProgressBar progress = this.gui.getProgressBar();
		if (progress.isVisible()){
			progress.setString("");
			progress.setIndeterminate(false);
			progress.setVisible(false);
		 	return true;
		} else {
			return false;
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// essentially "presses the exit button"
		this.actionPerformed(new ActionEvent(this.gui.getExitButton(), ActionEvent.ACTION_PERFORMED, null));	
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {}

	@Override
	public void componentShown(ComponentEvent arg0) {}

}
