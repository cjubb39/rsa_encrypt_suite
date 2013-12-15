package rsaEncrypt;

import java.math.BigInteger;
import java.util.Random;

/**
 * {@link Makekeys} is designed to randomly generate two RSA keys, one private
 * and one public. These key files are named based on the first input argument
 * as {arg1}.private and {arg1}.public. The algorithm used to pick primes is
 * probabilistic, yet by tuning a parameter, we are able to make the probability
 * of a false positive near infinitesimal.
 * 
 * @author Chae Jubb // ecj2122
 * @version 2.0
 * 
 */
public class MakeKeys {

	public static final int lowPrimeBitLength = 1500;
	public static final int highPrimeBitLength = 2000;
	public static final Random rnd = new Random(System.currentTimeMillis());

	/**
	 * We create an RSA keypair using the RSA algorithm, writing them to file
	 * 
	 * @param priKey
	 *            Should point to where private key file ought to be written
	 * @param pubKey
	 *            Should point to where public key file ought to be written
	 */
	public static KeyPair generateKeys() {
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
	 * Randomly generate prime number between given limits. Implemented by
	 * randomly picking number in range and checking if it is prime. Uses
	 * probabilistic algorithm to find prime. Thus we have a very small (near
	 * infinitesimal) chance of returning a non-prime. This probability is tuned
	 * as {@link Utilites.primalityParam}.
	 * 
	 * @param lower
	 *            Lower limit on prime to be returned
	 * @param upper
	 *            Upper limit on prime to be returned
	 * @return Prime number.
	 */
	/*private static int generatePrime(BigInteger lower, BigInteger upper) {
		// randomly check numbers between lower and upper
		BigInteger range = upper.subtract(lower), toRet;
		while (!Utilities.isPrime(toRet = (int) (range * Math.random()) + lower));
		return toRet;
	}*/

	/**
	 * Finds a suitable e for RSA scheme. This is the second part of the public
	 * key. We typically use a small integer for this to make encoding easier
	 * 
	 * @param number
	 *            Size of group in which we should find an e with multiplicative
	 *            inverse
	 * @return Appropriate e
	 */
	private static BigInteger calculateE(BigInteger number) {
		int i = 2; // minimum safe e is 3
		while (!(new BigInteger(String.valueOf(++i))).gcd(number).equals(BigInteger.ONE));
		return new BigInteger(String.valueOf(i));
	}
}
