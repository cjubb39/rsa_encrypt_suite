package shared;

import rsaEncrypt.KeyFile;
import java.io.Serializable;

public class User implements Serializable{
	
	public final long ID;
	private String firstName, lastName;
	private KeyFile pubKey;
	
	public User(String firstName, String lastName, KeyFile pubKey){
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = pubKey.hashCode();
		this.pubKey = pubKey;
	}
	
	public User(String fullName, KeyFile pubKey){
		this(fullName.split(" ")[0], fullName.split(" ")[1], pubKey);
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
