package negotiator.utility;

import java.math.BigInteger;

/**
 * Basic computations
 *
 */
public class Com {
	/**
	 * Computes factorial. Also works for big numbers like n>15
	 * 
	 * @param n
	 * @return n!
	 */
	public static BigInteger factorial(int n) {
		if (n <= 1)
			return BigInteger.ONE;
		return factorial(n - 1).multiply(BigInteger.valueOf(n));
	}

	/**
	 * @param n
	 * @param k
	 * @return n over k which equals to n! / (k! (n-k)!).
	 */
	public static BigInteger over(int n, int k) {
		return factorial(n).divide(factorial(k).multiply(factorial(n - k)));
	}
}
