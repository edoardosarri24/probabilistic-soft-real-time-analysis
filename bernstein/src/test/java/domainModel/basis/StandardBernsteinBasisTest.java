package domainModel.basis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class StandardBernsteinBasisTest {

    private final Random random = new Random(42);
    private static final double EPSILON = 1e-12;

    @Test
    public void knownValues() {
        LinearBernsteinBasis basis = new LinearBernsteinBasis(0, 1);
        assertThat(basis.eval(0, 1, 0.0)).isCloseTo(1.0, offset(EPSILON));
        assertThat(basis.eval(1, 1, 0.0)).isCloseTo(0.0, offset(EPSILON));
        assertThat(basis.eval(0, 1, 1.0)).isCloseTo(0.0, offset(EPSILON));
        assertThat(basis.eval(1, 1, 1.0)).isCloseTo(1.0, offset(EPSILON));
        assertThat(basis.eval(0, 1, 0.5)).isCloseTo(0.5, offset(EPSILON));
        assertThat(basis.eval(1, 1, 0.5)).isCloseTo(0.5, offset(EPSILON));
        assertThat(basis.eval(0, 2, 0.5)).isCloseTo(0.25, offset(EPSILON));
        assertThat(basis.eval(1, 2, 0.5)).isCloseTo(0.50, offset(EPSILON));
        assertThat(basis.eval(2, 2, 0.5)).isCloseTo(0.25, offset(EPSILON));
    }

    @Test
    public void partitionOfUnity() {
        LinearBernsteinBasis basis = new LinearBernsteinBasis(2.0, 5.0);
        int n = 10;
        for (int j=0; j < 100; j++) {
            double x = 2.0 + (5.0 - 2.0) * random.nextDouble();
            double sum = 0;
            for (int i=0; i <= n; i++) {
                double val = basis.eval(i, n, x);
                assertThat(val).isGreaterThanOrEqualTo(0.0);
                sum += val;
            }
            assertThat(sum).isCloseTo(1.0, offset(EPSILON));
        }
    }

    @Test
    public void invalidInterval() {
        assertThrows(IllegalArgumentException.class, () -> new LinearBernsteinBasis(10, 5));
    }
}
