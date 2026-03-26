package taskSet;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import sampler.ConstantSampler;
import utils.MyClock;

import static org.assertj.core.api.Assertions.*;

public class TaskSetTest {

    @Before
    public void setUp() {
        MyClock.reset();
    }

    @Test
    public void notPurelyPeriodic() {
        Task task0 = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(0)));
        Task task1 = new Task(
            5,
            1,
            new ConstantSampler(new BigDecimal(0)));
        Task task2 = new Task(
            5,
            5,
            new ConstantSampler(new BigDecimal(0)));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        assertThatThrownBy(() -> taskSet.purelyPeriodicCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task1.getId() + " non è puramente periodico: ha periodo PT0.005S e deadline PT0.001S");
    }

    @Test
    public void hyperbolicBoundTestTrue() {
        Task task0 = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(3)));
        Task task1 = new Task(
            5,
            5,
            new ConstantSampler(new BigDecimal(1)));
        Task task2 = new Task(
            50,
            50,
            new ConstantSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        assertThat(taskSet.hyperbolicBoundTest())
            .isTrue();
    }

    @Test
    public void hyperbolicBoundTestFalse() {
        Task task0 = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(5)));
        Task task1 = new Task(
            5,
            5,
            new ConstantSampler(new BigDecimal(1)));
        Task task2 = new Task(
            20,
            20,
            new ConstantSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        assertThat(taskSet.hyperbolicBoundTest())
            .isFalse();
    }

    @Test
    public void hyperbolicBoundTestWithNoPeriodicTask() {
        Task task0 = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(5)));
        Task task1 = new Task(
            5,
            4,
            new ConstantSampler(new BigDecimal(1)));
        Task task2 = new Task(
            20,
            20,
            new ConstantSampler(new BigDecimal(10)));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        assertThatThrownBy(() -> taskSet.hyperbolicBoundTest())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task1.getId() + " non è puramente periodico: ha periodo PT0.005S e deadline PT0.004S");
    }

}