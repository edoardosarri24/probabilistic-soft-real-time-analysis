import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ECDFTest {

    @Test
    public void constructor_nullInput_throwsException() {
        assertThatThrownBy(() -> new ECDF(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("I dati in input non possono essere null");
    }

    @Test
    public void constructor_nullValueInInput_throwsException() {
        List<Double> data = Arrays.asList(1.0, null, 2.0);
        assertThatThrownBy(() -> new ECDF(data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("I valori nulli non sono supportati");
    }

    @Test
    public void eval_emptyData_returnsZero() {
        ECDF ecdf = new ECDF(Collections.emptyList());
        assertThat(ecdf.eval(10.0)).isEqualTo(0.0);
    }

    @Test
    public void eval_singleValue() {
        ECDF ecdf = new ECDF(Collections.singletonList(5.0));
        assertThat(ecdf.eval(4.0)).isEqualTo(0.0);
        assertThat(ecdf.eval(5.0)).isEqualTo(1.0);
        assertThat(ecdf.eval(6.0)).isEqualTo(1.0);
    }

    @Test
    public void eval_multipleValues() {
        List<Double> data = Arrays.asList(1.0, 3.0, 5.0, 5.0, 7.0);
        ECDF ecdf = new ECDF(data);
        
        assertThat(ecdf.eval(0.0)).isEqualTo(0.0);
        assertThat(ecdf.eval(1.0)).isEqualTo(1.0 / 5.0);
        assertThat(ecdf.eval(2.0)).isEqualTo(1.0 / 5.0);
        assertThat(ecdf.eval(3.0)).isEqualTo(2.0 / 5.0);
        assertThat(ecdf.eval(4.0)).isEqualTo(2.0 / 5.0);
        assertThat(ecdf.eval(5.0)).isEqualTo(4.0 / 5.0);
        assertThat(ecdf.eval(6.0)).isEqualTo(4.0 / 5.0);
        assertThat(ecdf.eval(7.0)).isEqualTo(1.0);
        assertThat(ecdf.eval(8.0)).isEqualTo(1.0);
    }

    @Test
    public void eval_unsortedInput_isSortedInternal() {
        List<Double> data = Arrays.asList(7.0, 1.0, 5.0, 3.0, 5.0);
        ECDF ecdf = new ECDF(data);
        
        assertThat(ecdf.eval(0.0)).isEqualTo(0.0);
        assertThat(ecdf.eval(1.0)).isEqualTo(1.0 / 5.0);
        assertThat(ecdf.eval(3.0)).isEqualTo(2.0 / 5.0);
        assertThat(ecdf.eval(5.0)).isEqualTo(4.0 / 5.0);
        assertThat(ecdf.eval(7.0)).isEqualTo(1.0);
    }
}
