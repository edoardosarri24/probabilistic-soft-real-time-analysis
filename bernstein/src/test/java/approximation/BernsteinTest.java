package approximation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

import domainModel.ECDF;

public class BernsteinTest {

    @Test
    public void approximateECDFWithNormalBase_inputValidation() {
        ECDF ecdf = new ECDF(Collections.singletonList(0.5));
        
        assertThatThrownBy(() -> BernsteinFromECDF.approximateECDFWithNormalBase(null, 0.5, 10))
            .isInstanceOf(NullPointerException.class);
            
        assertThatThrownBy(() -> BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, -0.1, 10))
            .isInstanceOf(IllegalArgumentException.class);
            
        assertThatThrownBy(() -> BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, 1.1, 10))
            .isInstanceOf(IllegalArgumentException.class);
            
        assertThatThrownBy(() -> BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, 0.5, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void approximateECDFWithNormalBase_simpleCase() {
        // ECDF with a single value at 0.5
        ECDF ecdf = new ECDF(Collections.singletonList(0.5));
        
        // At x=0, result should be 0 because all ecdf.eval(n/degree) where n/degree < 0.5 are 0.
        // And B(n, degree)(0) is only non-zero for n=0. 0/degree = 0 < 0.5, so ecdf.eval(0) = 0.
        assertThat(BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, 0.0, 10)).isEqualTo(0.0);
        
        // At x=1, result should be 1.0 because ecdf.eval(n/degree) for all n is 1 if n/degree >= 0.5.
        // For degree=10, n=5..10 have ecdf.eval(n/10) = 1.
        // B(n, 10)(1) is 1 only for n=10. ecdf.eval(1) = 1.
        assertThat(BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, 1.0, 10)).isEqualTo(1.0);
    }

    @Test
    public void partitionOfUnity_ifECDFIsAlwaysOne() {
        // If we "fake" an ECDF that is 1 everywhere in the support
        // We can do this by having data at the very beginning of the support
        ECDF ecdf = new ECDF(Collections.singletonList(-1.0)); // All values >= -1.0 have ecdf=1
        
        // On [0,1], all ecdf.eval(n/d) will be 1
        for (double x = 0.0; x <= 1.0; x += 0.2) {
            assertThat(BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, x, 5)).isCloseTo(1.0, within(1e-9));
        }
    }

    @Test
    public void monotonicity_property() {
        // Bernstein polynomials of non-decreasing sequences are non-decreasing
        ECDF ecdf = new ECDF(Arrays.asList(0.2, 0.5, 0.8));
        
        double prev = -1.0;
        for (double x = 0.0; x <= 1.0; x += 0.1) {
            double current = BernsteinFromECDF.approximateECDFWithNormalBase(ecdf, x, 10);
            assertThat(current).isGreaterThanOrEqualTo(prev);
            prev = current;
        }
    }

    @Test
    public void approximateECDFWithLinearBase_supportMapping() {
        // ECDF with values in [10, 20]
        ECDF ecdf = new ECDF(Arrays.asList(12.0, 18.0));
        
        // Test boundaries
        assertThat(BernsteinFromECDF.approximateECDFWithLinearBase(ecdf, 10.0, 10, 10.0, 20.0)).isCloseTo(0.0, within(1e-9));
        assertThat(BernsteinFromECDF.approximateECDFWithLinearBase(ecdf, 20.0, 10, 10.0, 20.0)).isCloseTo(1.0, within(1e-9));
        
        // Test midpoint roughly
        double mid = BernsteinFromECDF.approximateECDFWithLinearBase(ecdf, 15.0, 10, 10.0, 20.0);
        assertThat(mid).isBetween(0.0, 1.0);
    }

    @Test
    public void approximateECDFWithExponentialBase_atInfinity() {
        ECDF ecdf = new ECDF(Arrays.asList(1.0, 2.0, 3.0));
        
        // For very large x, it should go to 1.0
        // basis for n=0: (1-exp(-x))^d -> 1
        // basis for n>0: exp(-nx) * (1-exp(-x))^(d-n) -> 0
        // result = 1.0 * (basis for n=0) + sum (ecdf * basis for n>0) -> 1.0
        assertThat(BernsteinFromECDF.approximateECDFWithExponentialBase(ecdf, 100.0, 10)).isCloseTo(1.0, within(1e-5));
    }

    @Test
    public void approximateEPDFWithNormalBase_isDensity() {
        // A simple ECDF
        ECDF ecdf = new ECDF(Arrays.asList(0.2, 0.4, 0.6, 0.8));
        
        // PDF should be non-negative (mostly, Bernstein can have small oscillations but for CDF it should be monotonic)
        for (double x = 0.0; x <= 1.0; x += 0.1) {
            assertThat(BernsteinFromECDF.approximateEPDFWithNormalBase(ecdf, x, 10)).isGreaterThanOrEqualTo(0.0);
        }
    }

    @Test
    public void approximateEPDFWithLinearBase_isDensity() {
        ECDF ecdf = new ECDF(Arrays.asList(12.0, 15.0, 18.0));
        
        for (double x = 10.0; x <= 20.0; x += 1.0) {
            assertThat(BernsteinFromECDF.approximateEPDFWithLinearBase(ecdf, x, 10, 10.0, 20.0)).isGreaterThanOrEqualTo(0.0);
        }
    }

    @Test
    public void approximateEPDFWithExponentialBase_isDensity() {
        ECDF ecdf = new ECDF(Arrays.asList(1.0, 2.0, 5.0));
        
        for (double x = 0.0; x <= 10.0; x += 1.0) {
            assertThat(BernsteinFromECDF.approximateEPDFWithExponentialBase(ecdf, x, 10)).isGreaterThanOrEqualTo(0.0);
        }
    }
}
