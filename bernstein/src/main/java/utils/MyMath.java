package utils;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class MyMath {

    // Constructor
    private MyMath() {}

    private static final Map<Integer, BigInteger[]> cache = new ConcurrentHashMap<>();

    // Methods
    /**
     * Calculate the binomial coefficient "n choose k".
     */
    public static BigInteger binomialCoefficient(int n, int k) {
        // Checks input.
        if (k < 0 || k > n)
            return BigInteger.ZERO;
        if (k == 0 || k == n)
            return BigInteger.ONE;
        // Search in cache.
        BigInteger[] cacheResult = cache.get(n);
        if (Objects.nonNull(cacheResult) && Objects.nonNull(cacheResult[k]))
            return cacheResult[k];
        // New value calculation.
        // Exploits the symmetry ((n,k) = (n,n-k)) to reduce the number of iterations.
        int effectiveK = (k > n/2) ? n-k : k;
        BigInteger result = BigInteger.ONE;
        for (int i=1; i <= effectiveK; i++) {
            result = result
                .multiply(BigInteger.valueOf(n-i+1))
                .divide(BigInteger.valueOf(i));
        }
        // Store in cache if not too large
        if (n < 1000) {
            cache.computeIfAbsent(n, key -> new BigInteger[key+1])[k] = result;
            // Store the symmetric one too
            cache.get(n)[n-k] = result;
        }
        return result;
    }

}
