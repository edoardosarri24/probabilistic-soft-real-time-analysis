package utils;

import java.math.BigDecimal;
import java.time.Duration;
import org.oristool.simulator.samplers.Sampler;

public class SampleDuration {
    /**
     * Samples a duration in milliseconds from the given sampler and returns it as a {@link Duration} object.
     * @param sampler The sampler from which to sample the value
     * @return The sampled duration as a {@link Duration} object express in nanoseconds.
     */
    public static Duration sample(Sampler sampler) {
        BigDecimal sample = sampler.getSample();
        sample = sample.multiply(BigDecimal.TEN.pow(6));
        return Duration.ofNanos(sample.longValue());
    }

}