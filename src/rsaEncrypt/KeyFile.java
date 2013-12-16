package rsaEncrypt;

import java.math.BigInteger;

public class KeyFile implements java.io.Serializable {

	private static final long serialVersionUID = 6617843965288418253L;
	private final BigInteger groupSize, key;
	
	public KeyFile(BigInteger groupSize, BigInteger key){
		this.groupSize = groupSize;
		this.key = key;
	}
	
	public KeyFile(long groupSize, long key){
		this(BigInteger.valueOf(groupSize), BigInteger.valueOf(key));
	}
	
	public BigInteger getGroupSize(){
		return this.groupSize;
	}
	
	public BigInteger getKey(){
		return this.key;
	}
	
	//TODO MAKE BETTER HASH FUNCTION
	public int hashCode(){
		return Math.abs(this.key.xor(this.groupSize).hashCode());
	}
}
