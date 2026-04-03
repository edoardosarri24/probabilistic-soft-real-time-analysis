

import domainModel.BernsteinPolynomial;
import domainModel.basis.LinearBernsteinBasis;
import org.junit.jupiter.api.Test;

public class BernsteinPolynomialBenchmarkTest {

    @Test
    public void benchmark() {
        // Initialization.
        int degree = 100;
        double[] coeffs = new double[degree + 1];
        for (int i=0; i <= degree; i++)
            coeffs[i] = Math.random();
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 1)
        );
        // Profiling.
        int iterations = 1000;
        long start = System.nanoTime();
        double sum = 0;
        for (int i = 0; i < iterations; i++)
            sum += p.eval(0.5); 
        long end = System.nanoTime();
        // Printing results.
        System.out.println("\n--- Bernstein Benchmark Results ---");
        System.out.println("Degree: " + degree);
        System.out.println("Iterations: " + iterations);
        System.out.println("Total Time: " + (end - start) / 1e6 + " ms");
        System.out.println("Average Time per eval: " + (double)(end - start) / iterations / 1e3 + " µs");
        System.out.println("Dummy sum (prevents JIT optimization): " + sum);
        System.out.println("-----------------------------------\n");
    }
}
