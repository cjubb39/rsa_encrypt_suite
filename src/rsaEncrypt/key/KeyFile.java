package rsaEncrypt.key;

import java.math.BigInteger;

/**
 * Holds a single key and the group size for the corresponding RSA computations
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class KeyFile implements java.io.Serializable {

	private static final long serialVersionUID = 6617843965288418253L;
	private final BigInteger groupSize, key;

	/**
	 * Constructor using BigIntegers
	 * 
	 * @param groupSize
	 *          RSA "n"
	 * @param key
	 *          RSA "e" or "d", depending on type
	 */
	public KeyFile(BigInteger groupSize, BigInteger key){
		this.groupSize = groupSize;
		this.key = key;
	}

	/**
	 * Constructor using long ints.
	 * 
	 * @param groupSize
	 *          RSA "n"
	 * @param key
	 *          RSA "e" or "d", depending on type
	 */
	public KeyFile(long groupSize, long key){
		this(BigInteger.valueOf(groupSize), BigInteger.valueOf(key));
	}

	/**
	 * @return Group size; RSA "n"
	 */
	public BigInteger getGroupSize(){
		return this.groupSize;
	}

	/**
	 * @return Key; RSA "e" or "d"
	 */
	public BigInteger getKey(){
		return this.key;
	}

	// TODO MAKE BETTER HASH FUNCTION
	/**
	 * Hash of keyfile defined so only groupsize and key numbers affect it.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return Math.abs(this.key.xor(this.groupSize).hashCode());
	}
}
