package taskSet;

import org.junit.Before;
import org.junit.Test;
import exeptions.DeadlineMissedException;
import helper.ReflectionUtils;
import utils.MyClock;
import utils.sampler.ConstantSampler;

import java.math.BigDecimal;
import java.time.Duration;

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
    public void checkAndResetIf() {
        assertThatThrownBy(() -> this.task.relasePeriodTask())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + this.task.getId() + " ha superato la deadline");
    }

    @Test
    public void checkAndResetElse() {
        Duration remainingExecutionTimeBefore = (Duration) ReflectionUtils.getField(this.task, "remainingExecutionTime");
        assertThat(remainingExecutionTimeBefore)
            .isEqualTo(Duration.ofMillis(5));
            
        ReflectionUtils.setField(this.task, "remainingExecutionTime", Duration.ZERO);
        ReflectionUtils.setField(this.task, "isExecuted", true);
        
        assertThat(this.task.getIsExecuted())
            .isTrue();
            
        assertThatCode(() -> this.task.relasePeriodTask())
            .doesNotThrowAnyException();
            
        Duration remainingExecutionTimeAfter = (Duration) ReflectionUtils.getField(this.task, "remainingExecutionTime");
        assertThat(remainingExecutionTimeAfter)
            .isEqualTo(Duration.ofMillis(5));
        assertThat(this.task.getIsExecuted())
            .isFalse();
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

    @Test
    public void nextDeadline() {
        Task task = new Task(
            5,
            3,
            new ConstantSampler(new BigDecimal(1)));
        Duration output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(3));
        MyClock.getInstance().advanceTo(Duration.ofMillis(2));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(3));
        MyClock.getInstance().advanceTo(Duration.ofMillis(3));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(5));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(6));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(8));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(13));
        MyClock.getInstance().advanceTo(Duration.ofMillis(9));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(13));
    }

}