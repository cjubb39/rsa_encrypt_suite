package client;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;

import javax.swing.JMenuItem;
import javax.swing.JLabel;

import shared.StateAutoSaver;

import java.awt.GridLayout;

public class RSAEncryptGUI implements shared.Savable{

	private RSAEncryptGUIController controller;
	private JFrame mainGUI;
	private JLabel txtRecipient;
	private JTable emailTable;
	private JButton btnSend, btnClear;
	private JTextArea messageArea;
	private JMenuItem mntmExit;
	private JLabel lblGreeting;
	private JProgressBar progressBar;
	
	private ArrayList<UserProfile> profiles = null;
	private volatile int activeProfileIndex;
	private volatile ServerProfile activeServerProfile;
	
	public static final File profileSaveDir = new File("./data/client/");
	public static final File profileSave = new File("./data/client/profiles.dat");
	private JMenuItem mntmAddServer;
	private JMenuItem mntmViewServers;
	private JMenuItem mntmAddContact;
	private JMenuItem mntmViewContacts;
	private JMenuItem mntmExportPublicKey;
	private JMenuItem mntmExportPrivateKey;
	private JMenuItem mntmDeleteSelectedMessages;
	private JButton btnAddRecipient;
	
	private JPanel inboxPanel, messagePanel;
	
	private ClientMessage currentMessage;
	private JMenuItem mntmReceiveMessages;
	private InboxController inboxController;
	private JMenuItem mntmSetActiveServer;
	private JLabel curServerLabel;
	
	public static final long saveStateDelayMilli = 1000*60*15; // 15 minutes

	/**
	 * Create the application.
	 */
	public RSAEncryptGUI() {	
		this.loadProfile();
		this.controller = new RSAEncryptGUIController(this);
		this.initGUI();
		
		//this.getActiveProfile().getAddressBook().add(new User("Friend", "Uno", MakeKeys.generateKeys().getPub()));
		this.controller.updateManagers();
		
		this.currentMessage = new ClientMessage(this.getActiveProfile().getMe());
		
		this.inboxController = new InboxController(this.getActiveProfile().getMessages(), this.inboxPanel, 
				this.messagePanel, this.mntmDeleteSelectedMessages);
		this.getMainGUI().setVisible(true);
		

		//once we're good to go, add shutdown hook to autosave on quit
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
					saveState();
				}
			}
		);
		
		new Timer(true).schedule(new StateAutoSaver(this), saveStateDelayMilli, saveStateDelayMilli);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initGUI() {
		this.mainGUI = new JFrame("RSA Encryption Suite");
		this.mainGUI.setVisible(false);
		this.mainGUI.setPreferredSize(new Dimension(900	, 700));
		this.mainGUI.setSize(new Dimension(900, 700));
		this.mainGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mainGUI.addWindowListener(this.controller);
		this.mainGUI.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.mainGUI.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		// create header panel
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());
		
		this.lblGreeting = new JLabel("Greetings");
		//this.lblGreeting.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.lblGreeting.setText("Hello, " + this.profiles.get(0).getMe().getFirstName() + "!");
		headerPanel.add(this.lblGreeting, BorderLayout.WEST);
		
		this.progressBar = new JProgressBar();
		this.progressBar.setVisible(false);
		this.progressBar.setStringPainted(true);
		headerPanel.add(this.progressBar, BorderLayout.EAST);
		
		this.curServerLabel = new JLabel();
		this.curServerLabel.setText("    Current Server: " + this.getActiveServer());
		headerPanel.add(this.curServerLabel, BorderLayout.CENTER);
		
		headerPanel.setPreferredSize(new Dimension((int) headerPanel.getPreferredSize().getWidth(), 
				(int) (this.progressBar.getPreferredSize().getHeight() * 1.25)));
		this.mainGUI.getContentPane().add(headerPanel, BorderLayout.NORTH);
		
		// create inbox panel
		JPanel inboxPanelFull = new JPanel();
		tabbedPane.addTab("Inbox", null, inboxPanelFull, null);
		inboxPanelFull.setLayout(new GridLayout(2, 1));
		
		this.inboxPanel = new JPanel();
		this.inboxPanel.setLayout(new BorderLayout());
		inboxPanelFull.add(this.inboxPanel);
		
		this.messagePanel = new JPanel();
		this.messagePanel.setLayout(new BorderLayout());
		inboxPanelFull.add(this.messagePanel);
		
		// create compose panel
		JPanel composePanel = new JPanel();
		tabbedPane.addTab("Compose Message", null, composePanel, null);
		composePanel.setLayout(new BorderLayout(0, 0));
		
		// create to panel, and add recipients
		JPanel toPanel = new JPanel();
		composePanel.add(toPanel, BorderLayout.NORTH);
		toPanel.setLayout(new BorderLayout(0, 0));
		
		this.txtRecipient = new JLabel();
		this.txtRecipient.setText("Recipient: ");
		toPanel.add(txtRecipient, BorderLayout.WEST);
		
		this.btnAddRecipient = new JButton("Add Recipient");
		toPanel.add(this.btnAddRecipient, BorderLayout.EAST);
		this.btnAddRecipient.addActionListener(this.controller);
		
		//compose buttons (send/clear)
		JPanel compButPanel = new JPanel();
		composePanel.add(compButPanel, BorderLayout.SOUTH);
		compButPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		this.btnSend = new JButton("Send");
		this.btnSend.addActionListener(this.controller);
		compButPanel.add(this.btnSend);
		
		this.btnClear = new JButton("Clear");
		this.btnClear.addActionListener(this.controller);
		compButPanel.add(this.btnClear);
		
		JScrollPane scrollPane = new JScrollPane();
		composePanel.add(scrollPane, BorderLayout.CENTER);
		
		this.messageArea = new JTextArea();
		this.messageArea.setText("Enter Message Here");
		scrollPane.setViewportView(this.messageArea);
		
		
		// create menu Bar
		JMenuBar menuBar = new JMenuBar();
		this.mainGUI.setJMenuBar(menuBar);
		
		JMenu mnfileMenu = new JMenu("File");
		menuBar.add(mnfileMenu);
		
		this.mntmExportPrivateKey = new JMenuItem("Export Private Key");
		mnfileMenu.add(this.mntmExportPrivateKey);
		this.mntmExportPrivateKey.addActionListener(this.controller);
		
		this.mntmExportPublicKey = new JMenuItem("Export Public Key");
		mnfileMenu.add(this.mntmExportPublicKey);
		this.mntmExportPublicKey.addActionListener(this.controller);
		
		this.mntmExit = new JMenuItem("Exit");
		mnfileMenu.add(this.mntmExit);
		this.mntmExit.addActionListener(this.controller);
		
		JMenu mnMessage = new JMenu("Message");
		menuBar.add(mnMessage);
		
		this.mntmReceiveMessages = new JMenuItem("Receive Messages");
		mnMessage.add(this.mntmReceiveMessages);
		this.mntmReceiveMessages.addActionListener(this.controller);
		
		this.mntmDeleteSelectedMessages = new JMenuItem("Delete Selected Messages");
		mnMessage.add(this.mntmDeleteSelectedMessages);
		// actionListenere in this.inboxController
		
		JMenu mnServer = new JMenu("Server");
		menuBar.add(mnServer);
	
		this.mntmAddServer = new JMenuItem("Add New Server");
		mnServer.add(this.mntmAddServer);
		this.mntmAddServer.addActionListener(this.controller);
		
		this.mntmViewServers = new JMenuItem("View Server List");
		mnServer.add(this.mntmViewServers);
		this.mntmViewServers.addActionListener(this.controller);
		
		this.mntmSetActiveServer = new JMenuItem("Set Active Server");
		mnServer.add(this.mntmSetActiveServer);
		this.mntmSetActiveServer.addActionListener(this.controller);
		
		JMenu mnAddressBook = new JMenu("Address Book");
		menuBar.add(mnAddressBook);	
		
		this.mntmAddContact = new JMenuItem("Add Contact");
		mnAddressBook.add(this.mntmAddContact);
		this.mntmAddContact.addActionListener(this.controller);
		
		this.mntmViewContacts = new JMenuItem("View Contacts");
		mnAddressBook.add(this.mntmViewContacts);
		this.mntmViewContacts.addActionListener(this.controller);
		
		//this.mainGUI.setVisible(true);
	}
	
	public void exitSequence(){
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
			
		//this.saveProfile();
		System.exit(0);
	}
	
	@SuppressWarnings("unchecked")
	protected void loadProfile(){
		try{
			FileInputStream dataIn = new FileInputStream(RSAEncryptGUI.profileSave);

			ObjectInputStream din = new ObjectInputStream(dataIn);
			
			this.profiles = (ArrayList<UserProfile>) din.readObject();
			
			din.close();
		} catch (FileNotFoundException e){
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (this.profiles == null){
			this.profiles = new ArrayList<UserProfile>();
			String name = JOptionPane.showInputDialog(null, "Enter First and Last Name:", "Create New Profile");
			this.profiles.add(new UserProfile(name));
		}
		
		//set active profile
		this.activeProfileIndex = 0;
		if (this.getActiveProfile().getServers().size() > 0){
			this.setActiveServer(this.getActiveProfile().getServers().getData().get(0));
		}
	}
	
	public void saveState(){
		this.saveProfile();
	}
	
	protected void saveProfile(){
		try {
			if (this.profiles != null && this.profiles.size() > 0){
				RSAEncryptGUI.profileSaveDir.mkdirs();
				FileOutputStream dataOut = new FileOutputStream(RSAEncryptGUI.profileSave);
				ObjectOutputStream dout = new ObjectOutputStream(dataOut);
				dout.writeObject(this.profiles);
				dout.close();
				dataOut.close();
			}
			System.out.println("State saved");
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public ServerProfile getActiveServer(){
		return (this.activeServerProfile != null) ? this.activeServerProfile : new ServerProfile("",0,"No Active Server");
	}
	
	public void setActiveServer(ServerProfile serv){
		this.activeServerProfile = serv;
		try{
			this.curServerLabel.setText("    Current Server: " + this.getActiveServer());
		} catch (Exception e){}
		
		 //move new active server to front so it persists
		ArrayList<ServerProfile> temp = this.getActiveProfile().getServers().getData();
		Collections.swap(temp, temp.indexOf(serv), 0);
	}
	
	public ClientMessage resetCurrentMessage(){
		return this.currentMessage = new ClientMessage(this.getActiveProfile().getMe());
	}
	
	public UserProfile getActiveProfile(){
		return this.profiles.get(this.activeProfileIndex);
	}

	public RSAEncryptGUIController getController() {
		return this.controller;
	}

	public JFrame getMainGUI() {
		return this.mainGUI;
	}

	public JLabel getTxtRecipient() {
		return this.txtRecipient;
	}

	public JTable getEmailTable() {
		return this.emailTable;
	}

	public JButton getSendButton() {
		return this.btnSend;
	}

	public JButton getClearButton() {
		return this.btnClear;
	}
	
	public JMenuItem getExitButton(){
		return this.mntmExit;
	}

	public JTextArea getMessageArea() {
		return this.messageArea;
	}

	public JLabel getLblGreeting() {
		return this.lblGreeting;
	}

	public ArrayList<UserProfile> getProfiles() {
		return this.profiles;
	}

	public JMenuItem getAddServer() {
		return this.mntmAddServer;
	}

	public JMenuItem getViewServers() {
		return this.mntmViewServers;
	}

	public JMenuItem getAddContact() {
		return this.mntmAddContact;
	}

	public JMenuItem getViewContacts() {
		return this.mntmViewContacts;
	}

	public JMenuItem getExportPublicKey() {
		return this.mntmExportPublicKey;
	}

	public JMenuItem getExportPrivateKey() {
		return this.mntmExportPrivateKey;
	}
	
	public JButton getAddRecipientButton(){
		return this.btnAddRecipient;
	}
	
	public ClientMessage getCurrentMessage(){
		return this.currentMessage;
	}
	
	public JMenuItem getReceiveMessagesButton(){
		return this.mntmReceiveMessages;
	}
	
	public InboxController getInboxController(){
		return this.inboxController;
	}
	
	public JMenuItem getSetActiveServerButton(){
		return this.mntmSetActiveServer;
	}
	
	public JProgressBar getProgressBar(){
		return this.progressBar;
	}
}
