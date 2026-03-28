package utils;

import java.math.BigDecimal;
import java.time.Duration;
import org.oristool.simulator.samplers.Sampler;

public class SampleDuration {
    /**
     * Samples a duration in nanoseconds from the given sampler and returns it as a {@link Duration} object.
     * It's usefull to abstract the Duration and milliseconds/nanoseconds relation.
     */
    public static Duration sample(Sampler sampler) {
        MyUtils.requireNonNull(sampler, "sampler");
        BigDecimal sample = sampler.getSample();
        sample = sample.multiply(BigDecimal.TEN.pow(6));
        return Duration.ofNanos(sample.longValue());
    }

}