package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for mathematical operations, no threads-safe.
 */
public final class MyMath {

    private static final Map<Integer, double[]> cache = new HashMap<>();

    // Constructor
    private MyMath() {}


    // Methods
    /**
     * Calculate the binomial coefficient "n choose k".
     */
    public static double binomialCoefficient(int n, int k) {
        // Checks input.
        if (k < 0 || k > n)
            return 0.0;
        if (k == 0 || k == n)
            return 1.0;
        // Exploits the symmetry ((n,k) = (n,n-k)) to reduce the number of iterations.
        int optimizedK = (k > n/2) ? n-k : k;
        // Search in cache.
        double[] cacheResult = cache.get(n);
        if (Objects.nonNull(cacheResult) && cacheResult[optimizedK] != 0.0)
            return cacheResult[optimizedK];
        // New value calculation.
        double result = 1.0;
        for (int i=1; i <= optimizedK; i++)
            result = result * ((double) (n-i+1) / i);
        // Store the value and the symmetric one in cache if not too large
        if (n < 1000) {
            double[] row = cache.computeIfAbsent(n, key -> new double[key/2 + 1]);
            row[optimizedK] = result;
        }
        return result;
    }

    /**
     * Exponential function with integer exponent.
     */
    public static double intPow(double base, int exp) {
        // Checks input.
        if (exp < 0)
            throw new IllegalArgumentException("Esponente negativo non supportato: " + exp);
        // Base cases.
        if (exp == 0)
            return 1.0;
        if (exp == 1)
            return base;
        // Result.
        double result = 1.0;
        while (exp > 0) {
            if ((exp & 1) == 1)
                result *= base;
            base *= base;
            exp >>= 1;
        }
        return result;
    }

}
