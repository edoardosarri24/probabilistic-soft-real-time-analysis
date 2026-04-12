import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.Test;

import polynomial.BernsteinPolynomial;
import polynomial.MonomialPolynomial;

public class BernsteinFromMonomialTest {

    @Test
    public void testExactConversion_DegreeOne() {
        // P(x) = 1 + 2x
        double[] coefficients = {1.0, 2.0};
        MonomialPolynomial mp = new MonomialPolynomial(coefficients);
        
        BernsteinPolynomial bp = BernsteinFromMonomial.withDirectCoefficientConversion(mp);
        BernsteinPolynomial bpMatrix = BernsteinFromMonomial.withMatrixInversion(mp);
        
        // B(x) = beta_0 * (1-x) + beta_1 * x
        // beta_0 = a_0 = 1.0
        // beta_1 = a_0 + a_1 = 1.0 + 2.0 = 3.0
        
        assertThat(bp.eval(0.0)).isCloseTo(1.0, within(1e-9));
        assertThat(bp.eval(0.5)).isCloseTo(2.0, within(1e-9));
        assertThat(bp.eval(1.0)).isCloseTo(3.0, within(1e-9));

        assertThat(bpMatrix.eval(0.0)).isCloseTo(bp.eval(0.0), within(1e-9));
        assertThat(bpMatrix.eval(0.5)).isCloseTo(bp.eval(0.5), within(1e-9));
        assertThat(bpMatrix.eval(1.0)).isCloseTo(bp.eval(1.0), within(1e-9));
    }

    @Test
    public void testExactConversion_DegreeTwo() {
        // P(x) = 1 + 2x + 3x^2
        double[] coefficients = {1.0, 2.0, 3.0};
        MonomialPolynomial mp = new MonomialPolynomial(coefficients);
        
        BernsteinPolynomial bp = BernsteinFromMonomial.withDirectCoefficientConversion(mp);
        BernsteinPolynomial bpMatrix = BernsteinFromMonomial.withMatrixInversion(mp);
        
        // beta_0 = a_0 = 1.0
        // beta_1 = a_0 + (1/2)*a_1 = 1.0 + 0.5*2.0 = 2.0
        // beta_2 = a_0 + a_1 + a_2 = 1.0 + 2.0 + 3.0 = 6.0
        
        assertThat(bp.eval(0.0)).isCloseTo(1.0, within(1e-9));
        assertThat(bp.eval(0.5)).isCloseTo(1.0 + 2.0*0.5 + 3.0*0.25, within(1e-9)); // 2.75
        assertThat(bp.eval(1.0)).isCloseTo(6.0, within(1e-9));

        assertThat(bpMatrix.eval(0.0)).isCloseTo(bp.eval(0.0), within(1e-9));
        assertThat(bpMatrix.eval(0.5)).isCloseTo(bp.eval(0.5), within(1e-9));
        assertThat(bpMatrix.eval(1.0)).isCloseTo(bp.eval(1.0), within(1e-9));
    }

    @Test
    public void testExactConversion_DegreeTwo_Shifted() {
        // P(x) = (x - 0.5)^2 = x^2 - x + 0.25
        double[] coefficients = {0.25, -1.0, 1.0};
        MonomialPolynomial mp = new MonomialPolynomial(coefficients);
        
        BernsteinPolynomial bp = BernsteinFromMonomial.withDirectCoefficientConversion(mp);
        BernsteinPolynomial bpMatrix = BernsteinFromMonomial.withMatrixInversion(mp);
        
        // beta_0 = a_0 = 0.25
        // beta_1 = a_0 + 0.5*a_1 = 0.25 - 0.5 = -0.25
        // beta_2 = a_0 + a_1 + a_2 = 0.25 - 1.0 + 1.0 = 0.25
        
        assertThat(bp.eval(0.0)).isCloseTo(0.25, within(1e-9));
        assertThat(bp.eval(0.5)).isCloseTo(0.0, within(1e-9));
        assertThat(bp.eval(1.0)).isCloseTo(0.25, within(1e-9));
        
        double[] bpCoeffs = bp.getCoefficient();
        assertThat(bpCoeffs[0]).isCloseTo(0.25, within(1e-9));
        assertThat(bpCoeffs[1]).isCloseTo(-0.25, within(1e-9));
        assertThat(bpCoeffs[2]).isCloseTo(0.25, within(1e-9));

        assertThat(bpMatrix.eval(0.5)).isCloseTo(0.0, within(1e-9));
        double[] bpMatrixCoeffs = bpMatrix.getCoefficient();
        assertThat(bpMatrixCoeffs).containsExactly(bpCoeffs);
    }
}
