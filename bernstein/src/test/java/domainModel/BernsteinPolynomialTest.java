package domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import domainModel.basis.LinearBernsteinBasis;
import org.junit.jupiter.api.Test;

public class BernsteinPolynomialTest {

    private static final double EPSILON = 1e-12;

    @Test
    public void constantPolynomial() {
        // P(x) = 5.0 (constant)
        // For any n, if all coefficients are C, the polynomial is constant C
        double[] coeffs = {5.0, 5.0, 5.0, 5.0};
        BernsteinPolynomial p = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        
        assertThat(p.eval(0.0)).isCloseTo(5.0, offset(EPSILON));
        assertThat(p.eval(0.5)).isCloseTo(5.0, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(5.0, offset(EPSILON));
    }

    @Test
    public void identityPolynomial() {
        // P(x) = x
        // For degree n, coefficients are beta_i = i/n
        int n = 4;
        double[] coeffs = new double[n + 1];
        for (int i = 0; i <= n; i++) {
            coeffs[i] = (double) i / n;
        }
        BernsteinPolynomial p = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        
        assertThat(p.eval(0.0)).isCloseTo(0.0, offset(EPSILON));
        assertThat(p.eval(0.25)).isCloseTo(0.25, offset(EPSILON));
        assertThat(p.eval(0.5)).isCloseTo(0.5, offset(EPSILON));
        assertThat(p.eval(0.75)).isCloseTo(0.75, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(1.0, offset(EPSILON));
    }

    @Test
    public void boundaries() {
        // P(0) = beta_0, P(1) = beta_n
        double[] coeffs = {1.0, 2.0, 3.0, 4.0};
        BernsteinPolynomial p = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        
        assertThat(p.eval(0.0)).isCloseTo(1.0, offset(EPSILON));
        assertThat(p.eval(1.0)).isCloseTo(4.0, offset(EPSILON));
    }

    @Test
    public void symmetry() {
        // P(x) with coeffs [a, b, c] at x 
        // should be equal to P(x) with coeffs [c, b, a] at 1-x
        double[] coeffs = {1.0, 5.0, 2.0};
        double[] coeffsRev = {2.0, 5.0, 1.0};
        
        BernsteinPolynomial p1 = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        BernsteinPolynomial p2 = new BernsteinPolynomial(coeffsRev, new LinearBernsteinBasis(0, 1));
        
        double x = 0.3;
        assertThat(p1.eval(x)).isCloseTo(p2.eval(1.0 - x), offset(EPSILON));
    }

    @Test
    public void quadraticKnownValue() {
        // P(x) = (1-x)^2 + 2*0.5*x(1-x) + 0*x^2
        // beta = [1.0, 0.5, 0.0]
        // At x=0.5: 0.25 + 2*0.5*0.25 + 0 = 0.25 + 0.25 = 0.5
        double[] coeffs = {1.0, 0.5, 0.0};
        BernsteinPolynomial p = new BernsteinPolynomial(coeffs, new LinearBernsteinBasis(0, 1));
        
        assertThat(p.eval(0.5)).isCloseTo(0.5, offset(EPSILON));
    }
}
