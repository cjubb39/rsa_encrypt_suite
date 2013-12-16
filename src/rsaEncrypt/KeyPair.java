package rsaEncrypt;

public class KeyPair implements java.io.Serializable {
	private final KeyFile pub;
	private final KeyFile priv;
	
	public KeyPair(KeyFile pub, KeyFile priv){
		this.pub = pub;
		this.priv = priv;
	}
	
	public KeyPair(){
		this(null, null);
	}

	/**
	 * @return the private Key
	 */
	public KeyFile getPriv() {
		return this.priv;
	}

	/**
	 * @return the public Key
	 */
	public KeyFile getPub() {
		return this.pub;
	}
}
