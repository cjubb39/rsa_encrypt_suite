package client;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;

import javax.swing.JMenuItem;
import javax.swing.JLabel;

public class RSAEncryptGUI {

	private RSAEncryptGUIController controller;
	private JFrame mainGUI;
	private JLabel txtRecipient;
	private JTable emailTable;
	private JButton btnSend, btnClear;
	private JTextArea messageArea;
	private JMenuItem mntmExit;
	private JLabel lblGreeting;
	
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
	private JButton btnAddRecipient;
	
	private JPanel inboxPanel;
	
	private ClientMessage currentMessage;
	private JMenuItem mntmReceiveMessages;
	private InboxController inboxController;

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
		
		this.inboxController = new InboxController(this.getActiveProfile().getMessages(), this.inboxPanel);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initGUI() {
		this.mainGUI = new JFrame("RSA Encryption Suite");
		this.mainGUI.setPreferredSize(new Dimension(700, 500));
		this.mainGUI.setSize(new Dimension(700, 500));
		this.mainGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mainGUI.addWindowListener(this.controller);
		this.mainGUI.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.mainGUI.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		this.inboxPanel = new JPanel();
		tabbedPane.addTab("Inbox", null, this.inboxPanel, null);
		this.inboxPanel.setLayout(new BorderLayout(0, 0));
		
		this.lblGreeting = new JLabel("Greetings");
		this.inboxPanel.add(this.lblGreeting, BorderLayout.NORTH);
		this.lblGreeting.setText("Hello, " + this.profiles.get(0).getMe().getFirstName() + "!");
		
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
		//this.txtRecipient.setColumns(25);
		
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
		
		JMenu mnServer = new JMenu("Server");
		menuBar.add(mnServer);
	
		this.mntmAddServer = new JMenuItem("Add");
		mnServer.add(this.mntmAddServer);
		this.mntmAddServer.addActionListener(this.controller);
		
		this.mntmViewServers = new JMenuItem("View Server List");
		mnServer.add(this.mntmViewServers);
		this.mntmViewServers.addActionListener(this.controller);
		
		JMenu mnAddressBook = new JMenu("Address Book");
		menuBar.add(mnAddressBook);	
		
		this.mntmAddContact = new JMenuItem("Add");
		mnAddressBook.add(this.mntmAddContact);
		this.mntmAddContact.addActionListener(this.controller);
		
		this.mntmViewContacts = new JMenuItem("View");
		mnAddressBook.add(this.mntmViewContacts);
		this.mntmViewContacts.addActionListener(this.controller);
		
		this.mainGUI.setVisible(true);
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
			this.activeServerProfile = this.getActiveProfile().getServers().get(0);
		}
System.out.println("Message total: " + this.getActiveProfile().getMessages().size());
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
		return this.activeServerProfile;
	}
	
	public void setActiveServer(ServerProfile serv){
		this.activeServerProfile = serv;
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
}
