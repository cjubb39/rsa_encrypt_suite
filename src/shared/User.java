package shared;

import java.io.Serializable;

import rsaEncrypt.key.KeyFile;

/**
 * Represents a user, both client- and server-side.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class User implements TableData, Serializable {

	private static final long serialVersionUID = 5125505408275243663L;
	public transient boolean delete;
	public final String firstName, lastName;
	public final long ID;
	private KeyFile pubKey;

	/**
	 * Constructor.
	 * 
	 * @param firstName
	 *          First name of user
	 * @param lastName
	 *          Last name of user
	 * @param pubKey
	 *          Public key of user
	 */
	public User(String firstName, String lastName, KeyFile pubKey){
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = pubKey.hashCode();
		this.pubKey = pubKey;
	}

	/**
	 * Constructor.
	 * 
	 * @param fullName
	 *          First name and last name separated by a space
	 * @param pubKey
	 *          Public key of user
	 */
	public User(String fullName, KeyFile pubKey){
		this(fullName.split(" ")[0], (fullName.split(" ").length > 1) ? fullName.split(" ")[1] : " ",
				pubKey);
	}

	/**
	 * @return String representation of user: "firstName lastName"
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.getFirstName() + " " + this.getLastName();
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName(){
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName(){
		return lastName;
	}

	/**
	 * @return the pubKey
	 */
	public KeyFile getPubKey(){
		return pubKey;
	}

	/**
	 * @param pubKey
	 *          the pubKey to set
	 */
	public void setPubKey(KeyFile pubKey){
		this.pubKey = pubKey;
	}

	/**
	 * @return the ID
	 */
	public long getID(){
		return ID;
	}

	@Override
	public boolean getDelete(){
		return this.delete;
	}

	@Override
	public void setDelete(boolean in){
		this.delete = in;
	}
}
