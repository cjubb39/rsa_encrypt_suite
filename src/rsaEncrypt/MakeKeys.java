package rsaEncrypt;

import java.math.BigInteger;
import java.util.Random;

import rsaEncrypt.key.KeyFile;
import rsaEncrypt.key.KeyPair;

/**
 * {@link rsaEncrypt.MakeKeys} is designed to randomly generate two RSA keys, one private
 * and one public. These key files are named based on the first input argument as
 * {arg1}.private and {arg1}.public. The algorithm used to pick primes is probabilistic,
 * yet by tuning a parameter, we are able to make the probability of a false positive near
 * infinitesimal.
 * 
 * @author Chae Jubb // ecj2122
 * @version 2.0
 * 
 */
public class MakeKeys {

	public static final int lowPrimeBitLength = 250;
	public static final int highPrimeBitLength = 2000;
	public static final Random rnd = new Random(System.currentTimeMillis());

	/**
	 * We create an RSA keypair using the RSA algorithm, writing them to file
	 * 
	 * @return Generated KeyPair
	 */
	public static KeyPair generateKeys(){
		BigInteger p = BigInteger.probablePrime(lowPrimeBitLength, rnd);
		BigInteger q = BigInteger.probablePrime(lowPrimeBitLength, rnd);
		BigInteger n = p.multiply(q);

		BigInteger groupSize;
		BigInteger e;
		e = calculateE(groupSize = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)));

		BigInteger d = e.modInverse(groupSize);

		return new KeyPair(new KeyFile(n, e), new KeyFile(n, d));
	}

	/**
	 * Finds a suitable e for RSA scheme. This is the second part of the public key. We
	 * typically use a small integer for this to make encoding easier
	 * 
	 * @param number
	 *          Size of group in which we should find an e with multiplicative inverse
	 * @return Appropriate e
	 */
	private static BigInteger calculateE(BigInteger number){
		int i = 2; // minimum safe e is 3
		while (!(new BigInteger(String.valueOf(++i))).gcd(number).equals(BigInteger.ONE));
		return new BigInteger(String.valueOf(i));
	}
}
