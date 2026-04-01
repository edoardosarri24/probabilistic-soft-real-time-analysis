package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class MyMathTest {

    @Test
    public void binomialCoefficient_smallValues() {
        assertThat(MyMath.binomialCoefficient(5, 0)).isEqualTo(1.0);
        assertThat(MyMath.binomialCoefficient(5, 5)).isEqualTo(1.0);
        assertThat(MyMath.binomialCoefficient(5, 1)).isEqualTo(5.0);
        assertThat(MyMath.binomialCoefficient(5, 2)).isEqualTo(10.0);
        assertThat(MyMath.binomialCoefficient(5, 3)).isEqualTo(10.0);
    }

    @Test
    public void binomialCoefficient_largeValues() {
        // 100 choose 50 is a very large number, well beyond long.
        // Value: 100,891,344,545,564,193,334,812,497,256
        double result = MyMath.binomialCoefficient(100, 50);
        assertThat(result).isGreaterThan(BigInteger.valueOf(Long.MAX_VALUE).doubleValue());
        assertThat(result).isEqualTo(1.0089134454556418E29);
    }

    @Test
    public void binomialCoefficient_edgeCases() {
        assertThat(MyMath.binomialCoefficient(6, 5)).isEqualTo(6.0);
        assertThat(MyMath.binomialCoefficient(5, -1)).isEqualTo(0.0);
    }

}
