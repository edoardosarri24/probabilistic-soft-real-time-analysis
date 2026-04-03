package domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import domainModel.basis.ExponentialBernsteinBasis;
import domainModel.basis.LinearBernsteinBasis;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class BernsteinPolynomialStochasticTest {

    private static final double EPSILON = 1e-12;
    private final Random random = new Random(42);

    @Test
    public void constantPolynomialLinearBasis() {
        double constant = 7.3;
        int n = 10;
        double[] coeffs = new double[n + 1];
        for (int i=0; i <= n; i++)
            coeffs[i] = constant;
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 10));
        for (int i=0; i < 100; i++) {
            double x = random.nextDouble() * 10.0;
            assertThat(p.eval(x)).isCloseTo(constant, offset(EPSILON));
        }
    }

    @Test
    public void constantPolynomialExponentialBasis() {
        double constant = 4.2;
        int n = 8;
        double[] coeffs = new double[n + 1];
        for (int i=0; i <= n; i++)
            coeffs[i] = constant;
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new ExponentialBernsteinBasis());
        for (int i=0; i < 100; i++) {
            double x = random.nextDouble() * 20.0;
            assertThat(p.eval(x)).isCloseTo(constant, offset(EPSILON));
        }
    }

    @Test
    public void partitionOfUnityProperty() {
        // If all coefficients are 1, the sum should be 1 everywhere due to partition of unity
        int n = 12;
        double[] coeffs = new double[n + 1];
        for (int i=0; i <= n; i++)
            coeffs[i] = 1.0;
        BernsteinPolynomial pLinear = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(-5, 5));
        BernsteinPolynomial pExp = new BernsteinPolynomial(
            coeffs,
            new ExponentialBernsteinBasis());
        for (int i = 0; i < 50; i++) {
            double xLinear = -5.0 + 10.0 * random.nextDouble();
            double xExp = random.nextDouble() * 10.0;
            assertThat(pLinear.eval(xLinear)).isCloseTo(1.0, offset(EPSILON));
            assertThat(pExp.eval(xExp)).isCloseTo(1.0, offset(EPSILON));
        }
    }
}
