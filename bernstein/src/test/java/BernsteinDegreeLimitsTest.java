
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import utils.MyMath;

/**
 * Tests to verify the degree limits of the Bernstein library as specified in the documentation.
 * These tests ensure that the 'double' precision limits are correctly handled.
 */
public class BernsteinDegreeLimitsTest {

    private static final double DOUBLE_EXACT_INTEGER_LIMIT = Math.pow(2, 53);

    @Test
    public void precisionLimit_n53_individualBinomialWithinLimit() {
        double bin = MyMath.binomialCoefficient(53, 26);
        assertThat(bin).isLessThan(DOUBLE_EXACT_INTEGER_LIMIT);
    }

    @Test
    public void precisionLimit_n54_sumExceedsExactLimit() {
        double sumOfCoefficients = Math.pow(2, 54);
        assertThat(sumOfCoefficients).isGreaterThan(DOUBLE_EXACT_INTEGER_LIMIT);
    }

    @Test
    public void precisionLimit_n57_individualBinomialExceedsLimit() {
        double bin = MyMath.binomialCoefficient(57, 28);
        assertThat(bin).isGreaterThan(DOUBLE_EXACT_INTEGER_LIMIT);
    }

    @Test
    public void overflowLimit_n1029_isFinite() {
        double bin = MyMath.binomialCoefficient(1029, 514);
        assertThat(bin).isFinite();
        assertThat(bin).isLessThan(Double.MAX_VALUE);
    }

    @Test
    public void overflowLimit_n1030_isInfinite() {
        double bin = MyMath.binomialCoefficient(1030, 515);
        assertThat(Double.isInfinite(bin)).isTrue();
    }

}
