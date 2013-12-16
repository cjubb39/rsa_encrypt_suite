package shared;

import rsaEncrypt.KeyFile;
import java.io.Serializable;

public class User implements Serializable{
	
	private static final long serialVersionUID = 5125505408275243663L;
	public final String firstName, lastName;
	public final long ID;
	private KeyFile pubKey;
	
	public User(String firstName, String lastName, KeyFile pubKey){
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = pubKey.hashCode();
		this.pubKey = pubKey;
	}
	
	public User(String fullName, KeyFile pubKey){
		this(fullName.split(" ")[0], (fullName.split(" ").length > 1) ? fullName.split(" ")[1] : " ", pubKey);
	}
	
	public String toString(){
		return this.getFirstName() + " " + this.getLastName();
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the pubKey
	 */
	public KeyFile getPubKey() {
		return pubKey;
	}

	/**
	 * @param pubKey the pubKey to set
	 */
	public void setPubKey(KeyFile pubKey) {
		this.pubKey = pubKey;
	}

	/**
	 * @return the ID
	 */
	public long getID() {
		return ID;
	}
}
