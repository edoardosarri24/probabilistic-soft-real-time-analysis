package domainModel.basis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class ExponentialBernsteinBasisTest {

    private final Random random = new Random(42);
    private static final double EPSILON = 1e-12;

    @Test
    public void knownValues() {
        double lambda = 1.0;
        ExponentialBernsteinBasis basis = new ExponentialBernsteinBasis(lambda);
        
        // At x=0, t = 1 - e^0 = 0.
        // B_{0,n}(0) should be 1, others 0.
        assertThat(basis.eval(0, 5, 0.0)).isCloseTo(1.0, offset(EPSILON));
        for (int i = 1; i <= 5; i++) {
            assertThat(basis.eval(i, 5, 0.0)).isCloseTo(0.0, offset(EPSILON));
        }

        // For large x, t -> 1.
        // B_{n,n}(large x) should be close to 1.
        assertThat(basis.eval(10, 10, 100.0)).isCloseTo(1.0, offset(EPSILON));
    }

    @Test
    public void partitionOfUnity() {
        ExponentialBernsteinBasis basis = new ExponentialBernsteinBasis(0.5);
        int n = 15;
        
        for (int j = 0; j < 100; j++) {
            double x = random.nextDouble() * 20.0; // Random x in [0, 20]
            double sum = 0;
            for (int i = 0; i <= n; i++) {
                double val = basis.eval(i, n, x);
                assertThat(val).isGreaterThanOrEqualTo(-EPSILON); // Small tolerance for precision
                sum += val;
            }
            assertThat(sum).isCloseTo(1.0, offset(EPSILON));
        }
    }

    @Test
    public void invalidLambda() {
        assertThrows(IllegalArgumentException.class, () -> new ExponentialBernsteinBasis(-0.1));
    }
}
