package domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import domainModel.basis.LinearBernsteinBasis;
import org.junit.jupiter.api.Test;

public class BernsteinPolynomialTest {

    private static final double EPSILON = 1e-12;

    @Test
    public void constantPolynomial() {
        double[] coeffs = {5.0, 5.0, 5.0, 5.0};
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 1));
        assertThat(p.eval(0.0)).isCloseTo(5.0, offset(EPSILON));
        assertThat(p.eval(0.5)).isCloseTo(5.0, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(5.0, offset(EPSILON));
    }

    @Test
    public void identityPolynomial() {
        int n = 4;
        double[] coeffs = new double[n + 1];
        for (int i = 0; i <= n; i++)
            coeffs[i] = (double) i / n;
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 1));
        assertThat(p.eval(0.0)).isCloseTo(0.0, offset(EPSILON));
        assertThat(p.eval(0.25)).isCloseTo(0.25, offset(EPSILON));
        assertThat(p.eval(0.5)).isCloseTo(0.5, offset(EPSILON));
        assertThat(p.eval(0.75)).isCloseTo(0.75, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(1.0, offset(EPSILON));
    }

    @Test
    public void boundaries() {
        double[] coeffs = {1.0, 2.0, 3.0, 4.0};
        BernsteinPolynomial p = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        assertThat(p.eval(0.0)).isCloseTo(1.0, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(4.0, offset(EPSILON));
    }

    @Test
    public void symmetry() {
        double[] coeffs = {1.0, 5.0, 2.0};
        double[] coeffsRev = {2.0, 5.0, 1.0};
        BernsteinPolynomial p1 = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 1));
        BernsteinPolynomial p2 = new BernsteinPolynomial(
            coeffsRev,
            new LinearBernsteinBasis(0, 1));
        double x = 0.3;
        assertThat(p1.eval(x)).isCloseTo(p2.eval(1.0 - x), offset(EPSILON));
    }

    @Test
    public void quadraticKnownValue() {
        double[] coeffs = {1.0, 0.5, 0.0};
        BernsteinPolynomial p = new BernsteinPolynomial(
            coeffs,
            new LinearBernsteinBasis(0, 1));
        assertThat(p.eval(0.5)).isCloseTo(0.5, offset(EPSILON));
    }
}
