package polynomial.bernsteinPolynomial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.Test;

import domainModel.polynomial.BernsteinPolynomial;
import domainModel.polynomial.bernsteinBasis.LinearBernsteinbasis;

public class BernsteinPolynomialTest {

    @Test
    public void testDegreeZero() {
        double[] coeffs = {2.5};
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        assertThat(bp.eval(0.0)).isEqualTo(2.5);
        assertThat(bp.eval(0.5)).isEqualTo(2.5);
        assertThat(bp.eval(1.0)).isEqualTo(2.5);
    }

    @Test
    public void testDegreeOne() {
        double[] coeffs = {1.0, 3.0}; // B(x) = 1.0*(1-x) + 3.0*x
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        assertThat(bp.eval(0.0)).isEqualTo(1.0);
        assertThat(bp.eval(0.5)).isEqualTo(2.0);
        assertThat(bp.eval(1.0)).isEqualTo(3.0);
    }

    @Test
    public void testBoundaries() {
        double[] coeffs = {0.1, 0.5, 0.9, 0.3}; // degree 3
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        assertThat(bp.eval(0.0)).isEqualTo(0.1);
        assertThat(bp.eval(1.0)).isEqualTo(0.3);
    }

    @Test
    public void testSumOfBasisUnity() {
        // sum of B_{i,n}(x) should be 1.0 for any x in [0,1]
        double[] coeffs = {1.0, 1.0, 1.0, 1.0, 1.0}; // B(x) = sum(B_{i,4}(x)) = 1.0
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        for (double x = 0.0; x <= 1.0; x += 0.1) {
            assertThat(bp.eval(x)).isCloseTo(1.0, within(1e-9));
        }
    }

    @Test
    public void testHighDegreeStability() {
        int degree = 100;
        double[] coeffs = new double[degree + 1];
        java.util.Arrays.fill(coeffs, 1.0);
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        for (double x = 0.0; x <= 1.0; x += 0.1) {
            assertThat(bp.eval(x)).isCloseTo(1.0, within(1e-9));
        }
    }

    @Test
    public void testVeryHighDegreeStability() {
        // Test near the limit where binomial(n, n/2) still fits in double (~1029 for double max exponent)
        int degree = 1000;
        double[] coeffs = new double[degree + 1];
        java.util.Arrays.fill(coeffs, 1.0);
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        // x = 0.5 is the worst case for underflow/overflow combination
        assertThat(bp.eval(0.5)).isCloseTo(1.0, within(1e-7));
    }

    @Test
    public void testInvalidRange() {
        double[] coeffs = {1.0, 2.0};
        BernsteinPolynomial bp = new BernsteinPolynomial(coeffs, new LinearBernsteinbasis(0.0, 1.0));
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> bp.eval(-0.01))
            .isInstanceOf(IllegalArgumentException.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> bp.eval(1.01))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
