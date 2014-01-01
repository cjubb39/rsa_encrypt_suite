package client.primary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import rsaEncrypt.key.KeyFile;
import shared.message.ServerMessage;
import shared.serverComm.CommBytes;
import client.message.InboxMessage;
import client.server.ServerHandler;
import client.server.ServerProfile;
import client.table.AddressBook;
import client.table.ServerList;
import client.table.gui.AddRecipientsGUI;

/**
 * Controller for Client GUI of RSA Encryption Suite. All actions on GUI should proceed
 * through this controller.
 * 
 * @author Chae Jubb
 * @version 2.0
 * 
 */
public class RSAEncryptGUIController implements ActionListener, WindowListener, ComponentListener,
		PropertyChangeListener {

	private final RSAEncryptGUI gui;
	private AddressBook contactManager;
	private ServerList serverManager;

	public static final JFileChooser fc = new JFileChooser("./");

	/**
	 * Creates new controller for specified GUI
	 * 
	 * @param gui
	 *          GUI to be controlled
	 */
	public RSAEncryptGUIController(RSAEncryptGUI gui){
		this.gui = gui;

		this.contactManager = this.gui.getActiveProfile().getAddressBook();
		this.serverManager = this.gui.getActiveProfile().getServers();
		this.updateManagers();
	}

	/**
	 * Make sure Contact and Server managers match those of active user profile
	 */
	public void updateManagers(){
		if (this.gui.getActiveProfile().getAddressBook().getData() != this.contactManager.getData()) {
			this.contactManager = new AddressBook(this.gui.getActiveProfile().getAddressBook().getData());
			this.contactManager.addPropertyChangeListener(this);
		}

		if (this.gui.getActiveProfile().getServers().getData() != this.serverManager.getData()) {
			this.serverManager = new ServerList(this.gui.getActiveProfile().getServers().getData());
			this.serverManager.addPropertyChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0){
		final JTextArea messageArea = this.gui.getMessageArea();

		this.updateManagers();

		// sort based on origin
		if (arg0.getSource().equals(this.gui.getClearButton())) {
			this.clearMessage();
		} else if (arg0.getSource().equals(this.gui.getSendButton())) {
			// send messages in different thread
			(new Thread() {
				public void run(){
					startProgressBar("Sending Message");
					prepareAndSendMessage(messageArea);
					stopProgressBar();
				}
			}).start();
		} else if (arg0.getSource().equals(this.gui.getExitButton())) {
			this.exitSequence();
		} else if (arg0.getSource().equals(this.gui.getAddContact())) {
			this.contactManager.addOne();
		} else if (arg0.getSource().equals(this.gui.getViewContacts())) {
			this.contactManager.viewAll();
		} else if (arg0.getSource().equals(this.gui.getAddServer())) {
			this.serverManager.addOne();
		} else if (arg0.getSource().equals(this.gui.getViewServers())) {
			this.serverManager.viewAll();
		} else if (arg0.getSource().equals(this.gui.getExportPublicKey())) {
			UserProfile active = this.gui.getActiveProfile();
			this.exportKey(active.getKp().getPub(), active.getMe().getFirstName() + "_"
					+ active.getMe().getLastName() + "_public");
		} else if (arg0.getSource().equals(this.gui.getExportPrivateKey())) {
			// we want to make sure user knows what they're doing when exporting Private key
			int option = JOptionPane
					.showConfirmDialog(
							this.gui.getMainGUI(),
							"Attempting to Export Private Key.  This is potentially very dangerous.  Are you sure you want to continue?",
							"Warning!", JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				UserProfile active = this.gui.getActiveProfile();
				this.exportKey(active.getKp().getPriv(), active.getMe().getFirstName() + "_"
						+ active.getMe().getLastName() + "_private");
			}
		} else if (arg0.getSource().equals(this.gui.getAddRecipientButton())) {
			this.addRecipientToCurrentMessage();
		} else if (arg0.getSource().equals(this.gui.getReceiveMessagesButton())) {
			// receive messages in new thread
			(new Thread() {
				public void run(){
					startProgressBar("Checking Messages");
					receiveMessages();
					stopProgressBar();
				}
			}).start();
		} else if (arg0.getSource().equals(this.gui.getSetActiveServerButton())) {
			this.gui.getActiveProfile().setActiveServer();
		}

	}

	/**
	 * Launches GUI from which recipients can be added to messages currently being composed
	 */
	private void addRecipientToCurrentMessage(){
		new AddRecipientsGUI(this.contactManager, this.gui.getCurrentMessage(),
				this.gui.getTxtRecipient());
	}

	/**
	 * Clears messages currently being composed with confirmation
	 */
	private void clearMessage(){
		int n = JOptionPane.showConfirmDialog(null, "Do you really want to clear?",
				"RSA Encryption Suite", JOptionPane.OK_CANCEL_OPTION);
		if (n == JOptionPane.OK_OPTION) {
			this.resetCompose();
		}
	}

	/**
	 * Takes GUI state in order to form and send message
	 * 
	 * @param message
	 *          Area to take text from
	 * @return True on success; false otherwise
	 */
	private boolean prepareAndSendMessage(JTextArea message){
		boolean toRet = false;
		if (this.serverManager.getData().size() == 0) {
			JOptionPane.showMessageDialog(this.gui.getMainGUI(), "Must Select Valid Active Server!",
					"Invalid Server", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (this.gui.getCurrentMessage().getRecipients().size() == 0) {
			return false;
		}

		// get proper message
		this.gui.getCurrentMessage().setMessage(this.gui.getMessageArea().getText());
		ServerMessage[] toSend = this.gui.getCurrentMessage().toServerMessages();

		toRet = this.sendMessage(toSend, this.gui.getActiveProfile(), this.gui.getActiveProfile()
				.getActiveServer());

		// only reset on success
		if (toRet) {
			this.resetCompose();
		}

		return toRet;
	}

	/**
	 * Send ServerMessages from given user to given server
	 * 
	 * @param toSend
	 *          Array of messages to send
	 * @param sender
	 *          UserProfile of sender
	 * @param server
	 *          ServerProfile of destination server
	 * @return True on successful send of all messages; false otherwise
	 */
	private boolean sendMessage(ServerMessage[] toSend, UserProfile sender, ServerProfile server){
		// set up list of messages
		List<ServerMessage> messages = Arrays.asList(toSend);

		boolean returnStatus = false;
		try {
			returnStatus = ServerHandler.connectAndAct(
					(byte) (CommBytes.addNewUser | CommBytes.sendMessage), this.gui.getActiveProfile()
							.getActiveServer(), this.gui.getActiveProfile(), messages, null);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return returnStatus;
	}

	/**
	 * Check active server for message
	 * 
	 * @return True on messages received; false otherwise
	 */
	public boolean receiveMessages(){
		List<ServerMessage> ret = new ArrayList<ServerMessage>();

		try {
			ServerHandler.connectAndAct((byte) (CommBytes.addNewUser | CommBytes.receiveMessage),
					this.gui.getActiveProfile().getActiveServer(), this.gui.getActiveProfile(), null, ret);
		} catch (IOException e) {
			return false;
		}

		// check if we have mail
		if (ret.size() == 0) return false;

		// convert servermessage to inboxmessage
		ArrayList<InboxMessage> toAdd = new ArrayList<InboxMessage>();
		for (ServerMessage sm : ret) {
			toAdd.add(new InboxMessage(sm, this.contactManager));
		}

		this.gui.getInboxController().addMultiple(toAdd);

		return true;
	}

	/**
	 * Reset GUI to prepare for new composition
	 */
	private void resetCompose(){
		this.gui.resetCurrentMessage();
		this.gui.getMessageArea().setText("");
		this.gui.getTxtRecipient().setText("Recipient: ");
	}

	/**
	 * Export KeyFile to user-specified location
	 * 
	 * @param kf
	 *          KeyFile to export
	 * @param fileName
	 *          Default location to save
	 */
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

	/**
	 * Exit Sequence. Calls this.gui.exitSequence();
	 */
	public void exitSequence(){
		this.gui.exitSequence();
	}

	/**
	 * Set active server of active profile
	 * 
	 * @param serv
	 *          New Active ServerProfile
	 */
	public void setActiveServer(ServerProfile serv){
		this.gui.getActiveProfile().setActiveServer(serv);
	}

	/**
	 * Start progress bar in indeterminate mode while action occurs
	 * 
	 * @param message
	 *          Background message of progress bar
	 * @return True on successful start; false otherwise
	 */
	public boolean startProgressBar(String message){
		JProgressBar progress = this.gui.getProgressBar();
		if (progress.isVisible()) {
			return false;
		} else {
			progress.setIndeterminate(true);
			progress.setString(message);
			progress.setVisible(true);
			return true;
		}
	}

	/**
	 * Start progress bar in indeterminate mode with generic backtext
	 * 
	 * @return True on successful start; false otherwise
	 */
	public boolean startProgressBar(){
		return this.startProgressBar("Working...");
	}

	/**
	 * Stop progress bar and hide
	 * 
	 * @return True on successful stop; false otherwise
	 */
	public boolean stopProgressBar(){
		JProgressBar progress = this.gui.getProgressBar();
		if (progress.isVisible()) {
			progress.setString("");
			progress.setIndeterminate(false);
			progress.setVisible(false);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent arg0){}

	/**
	 * Equates pressing "red x" with clicking exit button
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent arg0){
		// essentially "presses the exit button"
		this.actionPerformed(new ActionEvent(this.gui.getExitButton(), ActionEvent.ACTION_PERFORMED,
				null));
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent arg0){}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent arg0){}

	/**
	 * On action from ServerClass, check and update active server label in GUI
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt){
		if (evt.getNewValue().getClass() == ServerProfile.class) {
			this.gui.updateActiveServerLabel();
		}
	}
}
