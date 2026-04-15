package polynomial;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import domainModel.polynomial.BernsteinPolynomial;
import domainModel.polynomial.MonomialPolynomial;
import domainModel.polynomial.bernsteinBasis.LinearBernsteinbasis;

public class MonomialPolynomialTest {

    @Test
    public void constructor_inputValidation() {
        assertThatThrownBy(() -> new MonomialPolynomial(null))
            .isInstanceOf(NullPointerException.class);
            
        assertThatThrownBy(() -> new MonomialPolynomial(new double[]{}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void immutability_test() {
        double[] coeffs = {1.0, 2.0};
        MonomialPolynomial p = new MonomialPolynomial(coeffs);
        coeffs[0] = 5.0; // Try to modify the internal state
        assertThat(p.eval(0.0)).isEqualTo(1.0); // Should remain 1.0
    }

    @Test
    public void eval_constant() {
        MonomialPolynomial p = new MonomialPolynomial(new double[]{5.0});
        assertThat(p.eval(0.0)).isEqualTo(5.0);
        assertThat(p.eval(10.0)).isEqualTo(5.0);
    }

    @Test
    public void eval_linear() {
        // p(x) = 1 + 2x
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0, 2.0});
        assertThat(p.eval(0.0)).isEqualTo(1.0);
        assertThat(p.eval(1.0)).isEqualTo(3.0);
        assertThat(p.eval(2.5)).isEqualTo(6.0);
    }

    @Test
    public void eval_quadratic() {
        // p(x) = 1 + 2x + 3x^2
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0, 2.0, 3.0});
        assertThat(p.eval(0.0)).isEqualTo(1.0);
        assertThat(p.eval(1.0)).isEqualTo(6.0);
        assertThat(p.eval(2.0)).isEqualTo(1 + 4 + 12); // 17.0
    }

    @Test
    public void convolve_inputValidation() {
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0});
        assertThatThrownBy(() -> p.convolve(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void convolve_identity() {
        // p(x) = 1
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0});
        // q(x) = 1 + 2x
        MonomialPolynomial q = new MonomialPolynomial(new double[]{1.0, 2.0});
        
        MonomialPolynomial result = (MonomialPolynomial) p.convolve(q);
        assertThat(result.getCoefficient()).containsExactly(1.0, 2.0);
    }

    @Test
    public void convolve_zero() {
        // p(x) = 0
        MonomialPolynomial p = new MonomialPolynomial(new double[]{0.0});
        // q(x) = 1 + 2x
        MonomialPolynomial q = new MonomialPolynomial(new double[]{1.0, 2.0});
        
        MonomialPolynomial result = (MonomialPolynomial) p.convolve(q);
        assertThat(result.getCoefficient()).containsExactly(0.0, 0.0);
    }

    @Test
    public void convolve_linear() {
        // (1 + x) * (1 + x) = 1 + 2x + x^2
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0, 1.0});
        MonomialPolynomial q = new MonomialPolynomial(new double[]{1.0, 1.0});
        
        MonomialPolynomial result = (MonomialPolynomial) p.convolve(q);
        assertThat(result.getCoefficient()).containsExactly(1.0, 2.0, 1.0);
    }

    @Test
    public void convolve_complex() {
        // (1 + 2x) * (3 + 4x) = 3 + 10x + 8x^2
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0, 2.0});
        MonomialPolynomial q = new MonomialPolynomial(new double[]{3.0, 4.0});
        
        MonomialPolynomial result = (MonomialPolynomial) p.convolve(q);
        assertThat(result.getCoefficient()).containsExactly(3.0, 10.0, 8.0);
    }

    @Test
    public void convolve_higherDegree() {
        // (1 + x + x^2) * (1 - x) = 1 - x^3
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0, 1.0, 1.0});
        MonomialPolynomial q = new MonomialPolynomial(new double[]{1.0, -1.0});
        
        MonomialPolynomial result = (MonomialPolynomial) p.convolve(q);
        assertThat(result.getCoefficient()).containsExactly(1.0, 0.0, 0.0, -1.0);
    }

    @Test
    public void convolve_differentBase() {
        MonomialPolynomial p = new MonomialPolynomial(new double[]{1.0});
        BernsteinPolynomial q = new BernsteinPolynomial(new double[]{1.0}, new LinearBernsteinbasis(0.0, 1.0));
        assertThatThrownBy(() -> p.convolve(q))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot convolve a MonomialPolynomial with a different polynomial base");
    }
}
