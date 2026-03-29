package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class MyMathTest {

    @Test
    public void binomialCoefficient_smallValues() {
        assertThat(MyMath.binomialCoefficient(5, 0)).isEqualTo(BigInteger.ONE);
        assertThat(MyMath.binomialCoefficient(5, 5)).isEqualTo(BigInteger.ONE);
        assertThat(MyMath.binomialCoefficient(5, 1)).isEqualTo(BigInteger.valueOf(5));
        assertThat(MyMath.binomialCoefficient(5, 2)).isEqualTo(BigInteger.valueOf(10));
        assertThat(MyMath.binomialCoefficient(5, 3)).isEqualTo(BigInteger.valueOf(10));
    }

    @Test
    public void binomialCoefficient_largeValues() {
        // 100 choose 50 is a very large number, well beyond long.
        // Value: 100,891,344,545,564,193,334,812,497,256
        BigInteger result = MyMath.binomialCoefficient(100, 50);
        assertThat(result).isGreaterThan(BigInteger.valueOf(Long.MAX_VALUE));
        assertThat(result.toString()).isEqualTo("100891344545564193334812497256");
    }

    @Test
    public void binomialCoefficient_edgeCases() {
        assertThat(MyMath.binomialCoefficient(5, 6)).isEqualTo(BigInteger.ZERO);
        assertThat(MyMath.binomialCoefficient(5, -1)).isEqualTo(BigInteger.ZERO);
    }
}
