package sampler;

import java.math.BigDecimal;

import org.oristool.simulator.samplers.Sampler;
import utils.MyUtils;

/**
 * Sampler that always returns a constant value.
 */
public final class DeterministicSampler implements Sampler {

    private final BigDecimal value;

    public DeterministicSampler(BigDecimal value) {
        this.value = MyUtils.requireNonNull(value, "value");
    }

    @Override
    public BigDecimal getSample() {
        return this.value;
    }
}