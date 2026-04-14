package polynomial;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import domainModel.polynomial.MonomialPolynomial;

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
}
