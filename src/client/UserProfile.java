package client;

import java.util.ArrayList;

import rsaEncrypt.KeyPair;
import rsaEncrypt.MakeKeys;
import shared.User;

public class UserProfile implements java.io.Serializable {

	private static final long serialVersionUID = 2365682000982434656L;
	private final User me;
	//private ArrayList<User> addressBook;
	private AddressBook addressBook;
	//private ArrayList<ServerProfile> servers;
	private ServerList servers;
	private ArrayList<InboxMessage> messages;
	private final KeyPair kp;
	
	public UserProfile(User me, ArrayList<User> addressBook, ArrayList<ServerProfile> servers, 
			KeyPair kp, ArrayList<InboxMessage> messages){
		this.me = me;
		this.addressBook = new AddressBook(addressBook);
		this.servers = new ServerList(servers);
		this.kp = kp;
		this.messages = messages;
		
		this.addressBook.addOne(me);
	}
	
	public UserProfile(User me, ArrayList<User> addressBook, ArrayList<ServerProfile> servers){
		this(me, addressBook, servers, MakeKeys.generateKeys(), new ArrayList<InboxMessage>());
	}
	
	public UserProfile(User me, ArrayList<User> addressBook, ServerProfile server){
		this(me, addressBook, new ArrayList<ServerProfile>());
		this.servers.addOne(server);
	}
	
	public UserProfile(User me, User friend, ArrayList<ServerProfile> servers){
		this(me, new ArrayList<User>(), servers);
		this.addressBook.addOne(friend);
	}
	
	public UserProfile(User me){
		this(me, new ArrayList<User>(), new ArrayList<ServerProfile>());
	}
	
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
	public AddressBook getAddressBook() {
		return this.addressBook;
	}

	/**
	 * @param addressBook the addressBook to set
	 */
	public void setAddressBook(AddressBook addressBook) {
		this.addressBook = addressBook;
	}

	/**
	 * @return the servers
	 */
	public ServerList getServers() {
		return this.servers;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setServers(ServerList servers) {
		this.servers = servers;
	}

	/**
	 * @return the me
	 */
	public User getMe() {
		return this.me;
	}

	/**
	 * @return the kp
	 */
	public KeyPair getKp() {
		return this.kp;
	}

	public ArrayList<InboxMessage> getMessages() {
		return this.messages;
	}
}
