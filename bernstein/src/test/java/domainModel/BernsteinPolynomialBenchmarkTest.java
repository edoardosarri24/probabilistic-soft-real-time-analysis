package domainModel;

import domainModel.basis.LinearBernsteinBasis;
import org.junit.jupiter.api.Test;

public class BernsteinPolynomialBenchmarkTest {

    @Test
    public void benchmark() {
        // Test con grado elevato per accentuare le inefficienze
        int n = 100; 
        double[] coeffs = new double[n + 1];
        for (int i = 0; i <= n; i++) {
            coeffs[i] = Math.random();
        }
        
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs, 
            new LinearBernsteinBasis(0, 1)
        );

        int iterations = 1000;
        
        // Warm-up
        for (int i = 0; i < 100; i++) {
            p.eval(Math.random());
        }

        long start = System.nanoTime();
        double sum = 0;
        for (int i = 0; i < iterations; i++) {
            // Usiamo un valore fisso per evitare variabilità nel calcolo di Math.random() durante il loop
            sum += p.eval(0.5); 
        }
        long end = System.nanoTime();
        
        System.out.println("\n--- Bernstein Benchmark Results ---");
        System.out.println("Degree: " + n);
        System.out.println("Iterations: " + iterations);
        System.out.println("Total Time: " + (end - start) / 1e6 + " ms");
        System.out.println("Average Time per eval: " + (double)(end - start) / iterations / 1e3 + " µs");
        System.out.println("Dummy sum (prevents JIT optimization): " + sum);
        System.out.println("-----------------------------------\n");
    }
}
