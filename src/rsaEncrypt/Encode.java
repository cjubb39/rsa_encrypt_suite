package rsaEncrypt;

import java.math.BigInteger;

/**
 * {@link Encode} reads in an RSA key file and encodes (decodes) a message
 * (cyphertext) file. This dual use works because of mechanics of RSA scheme.
 * 
 * @author Chae Jubb // ecj2122
 * @version 2.0
 * 
 */
public class Encode {

	/**
	 * Main method to encode (or decode) a message (encoded message) from stdin
	 * using a supplied public (private) key and writes the cyphertext
	 * (plaintext) to stdout.
	 * 
	 * @param args
	 *            args[0] should point to keyfile, which should be formatted:
	 *            "N\nKEY" where KEY is the public (private) key.
	 */
	public static void main(String[] args) {

		// read key and such in
		String[] keyValues = null;

		BigInteger key = null, modulus = null;
		try {
			keyValues = IO.readFile(args[0]).split("\n");
			modulus = new BigInteger(keyValues[0]);
			key = new BigInteger(keyValues[1]);
			if (modulus.compareTo(key) < 0)
				throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("IMPROPERLY FORMATTED KEY FILE");
			System.exit(1);
		}

		String inString = null;
		// while still another integer, print out encoded (decoded) integer
		while (((inString = IO.prompt(""))) != null && !inString.equals("")) {
			System.out.println((new BigInteger(inString)).modPow(key, modulus));
		}
	}
}
