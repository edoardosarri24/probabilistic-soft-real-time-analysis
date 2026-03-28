package sampler;

import java.math.BigDecimal;

import org.oristool.simulator.samplers.Sampler;
import utils.MyUtils;

/**
 * Sampler that always returns a constant value.
 */
public final class ConstantSampler implements Sampler {

    private final BigDecimal value;

    public ConstantSampler(BigDecimal value) {
        this.value = MyUtils.requireNonNull(value, "value");
    }

    @Override
    public BigDecimal getSample() {
        return this.value;
    }
}