package taskSet;

import org.junit.Before;
import org.junit.Test;

import sampler.ConstantSampler;
import utils.MyClock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class TaskTest {

    private Task task;

    @Before
    public void setUP() {
        this.task = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(5)));
        MyClock.reset();
    }

    @Test
    public void purelyPeriodicCheckOK() {
        assertThatCode(() -> task.purelyPeriodicCheck())
            .doesNotThrowAnyException();
    }

    @Test
    public void purelyPeriodicCheck() {
        Task task = new Task(
            10,
            3,
            new ConstantSampler(new BigDecimal(1)));
        assertThatThrownBy(() -> task.purelyPeriodicCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task.getId() + " non è puramente periodico: ha periodo PT0.01S e deadline PT0.003S");
    }

    @Test
    public void utilizationFactor() {
        Task task = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(5)));
        assertThat(task.utilizationFactor())
            .isEqualTo(0.5);
    }

    @Test
    public void periodAndDealineCheckKo() {
        Task task = new Task(
            10,
            12,
            new ConstantSampler(new BigDecimal(5)));
        assertThatThrownBy(() -> task.periodAndDealineCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task "+ task.getId()
                + " ha periodo PT0.01S e deadline PT0.012S. Il periodo non può essere minore della deadline");
    }

    @Test
    public void periodAndDealineCheckOk() {
        Task task = new Task(
            10,
            8,
            new ConstantSampler(new BigDecimal(5)));
        assertThatCode(() -> task.periodAndDealineCheck())
            .doesNotThrowAnyException();
    }
}
