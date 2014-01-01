package rsaEncrypt.key;

/**
 * Holds a pair of corresponding KeyFiles: public and private.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class KeyPair implements java.io.Serializable {

	private static final long serialVersionUID = 2233823812786898088L;
	private final KeyFile pub;
	private final KeyFile priv;

	/**
	 * Constructor
	 * 
	 * @param pub
	 *          Public Key
	 * @param priv
	 *          Private Key
	 */
	public KeyPair(KeyFile pub, KeyFile priv){
		this.pub = pub;
		this.priv = priv;
	}

	/**
	 * EmptyConstructor. Uses null KeyFiles.
	 */
	public KeyPair(){
		this(null, null);
	}

	/**
	 * @return the private Key
	 */
	public KeyFile getPriv(){
		return this.priv;
	}

	/**
	 * @return the public Key
	 */
	public KeyFile getPub(){
		return this.pub;
	}
}
