package rsaEncrypt;

/**
 * Utilities used in RSA scheme. Includes a primality test, gcd calculator,
 * modular exponentiation O(log n), modular inversion
 * 
 * @author Chae Jubb // ecj2122
 * @version 2.0
 * 
 */
public final class Utilities {

	/**
	 * This parameter determines the probability of a false positive in the
	 * implementation of the isPrime primality test. That probability is given
	 * by 4^-primalityParam. Thus the current tuning of 10 gives probability of
	 * a false positive on the order of 10^-603
	 */
	public static final short primalityParam = 1000;

	/**
	 * Implementation of the Miller-Rabin primality test
	 * 
	 * @param test
	 *            Number to test
	 * @return False if composite. True if probably prime. The probability of a
	 *         false positive is related to {@link Utilities.primalityParam}.
	 */
	public static boolean isPrime(int test) {
		int s = 0, d = test - 1;

		// generate d and s
		while (d % 2 == 0) {
			++s;
			d >>= 1;
		}

		// witness loop
		for (int i = 0; i < primalityParam; i++) {
			int a = (int) ((test - 4) * Math.random()) + 2;
			int x = modularExp(a, d, test);
			if ((x == 1) || (x == test - 1))
				continue; // relatively prime here; to to next loop iteration

			boolean intermediateExit = false;
			for (int j = 0; j < s - 1; j++) {
				x = (x * x) % test; // x = x^2 mod test
				if (x == 1)
					return false; // composite
				if (x == test - 1) {
					intermediateExit = true; // goto next iteration of the loop
					break;
				}
			}

			// essentially check how we got to this point
			if (!intermediateExit) {
				return false;
			}
		}

		return true; // probably prime
	}

	/**
	 * Compute modular exponentiation in O(log exp).
	 * 
	 * @param base
	 *            Base of exponentiation. Limited to MAX SIZE OF LONG.
	 * @param exp
	 *            Exponent of exponentiation. Limited to MAX SIZE OF LONG.
	 * @param modulus
	 *            Modulus of exponentiation. Limited to MAX SIZE OF INT.
	 * @return result of exponentation base^exp % modulus
	 */
	public static int modularExp(long base, long exp, int modulus) {
		base = base % modulus; // make sure we're not doing extra work

		int toRet = 1;
		long[] modExpResults = new long[(int) log2(exp + 1) + 1]; // mer[i] =
																	// base^2^i
		// seed array
		modExpResults[0] = (base % modulus); // base^1; cast okay because mod
												// limits to less than int

		// fill out rest of array by continuously doubling
		for (int i = 1; i < modExpResults.length; i++) {
			modExpResults[i] = (modExpResults[i - 1] * modExpResults[i - 1])
					% modulus;
		}

		// multiply appropriate factors together
		int counter = 0;
		while (exp > 0) {
			if ((exp & 1) == 1) {
				toRet = (int) ((toRet * modExpResults[counter]) % modulus);
				// cast is okay because modulus is int
			}
			++counter;
			exp >>= 1;
		}

		return toRet;
	}

	/**
	 * Returns modular inverse of given number with given modulus
	 * 
	 * @param num
	 *            Number to invert
	 * @param modulus
	 *            Modulus under which to invert
	 * @return Modular inverse
	 */
	public static int modularInvert(int num, int modulus) {
		int tmp; // convert to positive member of equivalence class if necessary
		return ((tmp = (extendedEuclidian(modulus, num)[1] % modulus)) > 0) ? tmp
				: tmp + modulus;
	}

	/**
	 * Runs extended Euclidian algorithm. We can input <number, modulus> to
	 * calculate number^-1 % modulus iff we already know gcd(number, modulus) =
	 * 1. In this case, number^-1 is return[1].
	 * 
	 * @param num1
	 *            First number
	 * @param num2
	 *            Second number
	 * @return Array of three ints. {GCD, X, Y} where X,Y are from num1*X +
	 *         num2*Y = gcd(num1,num2)
	 */
	public static int[] extendedEuclidian(int num1, int num2) {
		int a = 1, b = 0;
		int prevA = 0, prevB = 1;

		int quotient, remainder, m, n;

		while (num1 != 0) {
			// compute q and r
			quotient = num2 / num1;
			remainder = num2 % num1;

			// mod--sorta
			m = prevA - quotient * a;
			n = prevB - quotient * b;

			// prepare for next iteration
			prevA = a;
			prevB = b;
			a = m;
			b = n;
			num2 = num1;
			num1 = remainder;
		}

		return new int[] { num2, prevB, prevA };
	}

	/**
	 * Approximation of log_2. Accuracy limited by floating point arithmetic.
	 * 
	 * @param in
	 *            Number on which to compute log_2
	 * @return Log_2 of input
	 */
	public static double log2(double in) {
		return Math.log(in) / Math.log(2);
	}

	/**
	 * Implemented recursively with Euclidian Algorithm
	 * 
	 * @param x
	 *            First number
	 * @param y
	 *            Second number
	 * @return GCD of x and y
	 */
	public static long gcd(long x, long y) {
		return (y == 0) ? x : gcd(y, x % y);
	}
}
