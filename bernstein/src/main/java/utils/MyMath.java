package utils;

import java.math.BigInteger;

public final class MyMath {

    // Constructor
    private MyMath() {}

    // Methods
    /**
     * Calculate the binomial coefficient "n choose k".
     */
    public static BigInteger binomialCoefficient(int n, int k) {
        // Checks
        if (k < 0 || k > n)
            return BigInteger.ZERO;
        if (k == 0 || k == n)
            return BigInteger.ONE;
        // Exploits the symmetry ((n,k) = (n,n-k)) to reduce the number of iterations.
        if (k > n / 2)
            k = n - k;
        //Calculus
        BigInteger result = BigInteger.ONE;
        for (int j = 1; j <= k; j++) {
            result = result
                .multiply(BigInteger.valueOf(n - j + 1))
                .divide(BigInteger.valueOf(j));
        }
        return result;
    }

}
