package taskSet;

import org.junit.Before;
import org.junit.Test;

import sampler.ConstantSampler;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

public class JobTest {

    private Task task;

    @Before
    public void setUP() {
        this.task = new Task(
            10,
            10,
            new ConstantSampler(new BigDecimal(5)));
    }

    @Test
    public void jobCreation() {
        Job job = task.releaseJob(Duration.ofMillis(10));
        assertThat(job.getReleaseTime()).isEqualTo(Duration.ofMillis(10));
        assertThat(job.getAbsoluteDeadline()).isEqualTo(Duration.ofMillis(20));
        assertThat(job.getRemainingExecutionTime()).isEqualTo(Duration.ofMillis(5));
        assertThat(job.isCompleted()).isFalse();
    }

    @Test
    public void jobExecutionPartial() {
        Job job = task.releaseJob(Duration.ZERO);
        Duration executed = job.execute(Duration.ofMillis(2));
        assertThat(executed).isEqualTo(Duration.ofMillis(2));
        assertThat(job.getRemainingExecutionTime()).isEqualTo(Duration.ofMillis(3));
        assertThat(job.isCompleted()).isFalse();
    }

    @Test
    public void jobExecutionComplete() {
        Job job = task.releaseJob(Duration.ZERO);
        Duration executed = job.execute(Duration.ofMillis(6));
        assertThat(executed).isEqualTo(Duration.ofMillis(5));
        assertThat(job.getRemainingExecutionTime()).isEqualTo(Duration.ZERO);
        assertThat(job.isCompleted()).isTrue();
    }

    @Test
    public void deadlineMiss() {
        Job job = task.releaseJob(Duration.ZERO);
        assertThat(job.isDeadlineMissed(Duration.ofMillis(11))).isTrue();
        
        job.execute(Duration.ofMillis(5));
        assertThat(job.isCompleted()).isTrue();
        assertThat(job.isDeadlineMissed(Duration.ofMillis(11))).isFalse();
    }
}
