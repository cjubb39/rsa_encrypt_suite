package client.primary;

import java.util.ArrayList;

import client.message.InboxMessage;
import client.server.ServerProfile;
import client.table.AddressBook;
import client.table.ServerList;
import rsaEncrypt.key.KeyPair;
import rsaEncrypt.MakeKeys;
import shared.User;

/**
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class UserProfile implements java.io.Serializable {

	private static final long serialVersionUID = 2365682000982434656L;
	private final User me;
	private AddressBook addressBook;
	private ServerList servers;
	private ArrayList<InboxMessage> messages;
	private final KeyPair kp;

	/**
	 * Constructor
	 * 
	 * @param me
	 *          User for profile
	 * @param addressBook
	 *          AddressBook containing user's contacts
	 * @param servers
	 *          ServerList containing user's server
	 * @param kp
	 *          User's keypair
	 * @param messages
	 *          List of user's InboxMessages
	 */
	public UserProfile(User me, AddressBook addressBook, ServerList servers, KeyPair kp,
			ArrayList<InboxMessage> messages){
		this.me = me;
		this.addressBook = addressBook;
		this.servers = servers;
		this.kp = kp;
		this.messages = messages;

		this.addressBook.addOne(me);
	}

	/**
	 * Constructor. Generates new RSA Keypair and uses empty list of inbox messages
	 * 
	 * @param me
	 *          User for profile
	 * @param addressBook
	 *          AddressBook containing user's contacts
	 * @param servers
	 *          ServerList containing user's server
	 */
	public UserProfile(User me, AddressBook addressBook, ServerList servers){
		this(me, addressBook, servers, MakeKeys.generateKeys(), new ArrayList<InboxMessage>());
	}

	/**
	 * Constructor. AddressBook, ServerList, Keypair, Messages all auto-generated.
	 * 
	 * @param name
	 *          Name of user.
	 */
	public UserProfile(String name){
		KeyPair kp = MakeKeys.generateKeys();

		this.me = new User(name, kp.getPub());
		this.addressBook = new AddressBook();
		this.servers = new ServerList();
		this.kp = kp;
		this.messages = new ArrayList<InboxMessage>();

		this.addressBook.addOne(me);
	}

	/**
	 * @return the addressBook
	 */
	public AddressBook getAddressBook(){
		return this.addressBook;
	}

	/**
	 * @param addressBook
	 *          the addressBook to set
	 */
	public void setAddressBook(AddressBook addressBook){
		this.addressBook = addressBook;
	}

	/**
	 * @return the servers
	 */
	public ServerList getServers(){
		return this.servers;
	}

	/**
	 * @param servers
	 *          the servers to set
	 */
	public void setServers(ServerList servers){
		this.servers = servers;
	}

	/**
	 * @return User behind UserProfile
	 */
	public User getMe(){
		return this.me;
	}

	/**
	 * @return KeyPair of user
	 */
	public KeyPair getKp(){
		return this.kp;
	}

	/**
	 * @return List of InboxMessages
	 */
	public ArrayList<InboxMessage> getMessages(){
		return this.messages;
	}

	/**
	 * @return Active Server Profile
	 */
	public ServerProfile getActiveServer(){
		return this.servers.getActiveServer();
	}

	/**
	 * Set new active Server Profile using GUI
	 */
	public void setActiveServer(){
		this.servers.setActiveServer();
	}

	/**
	 * Set new active Server Profile
	 * 
	 * @param serv
	 *          new active server profile
	 */
	public void setActiveServer(ServerProfile serv){
		this.servers.setActiveServer(serv);
	}
}
