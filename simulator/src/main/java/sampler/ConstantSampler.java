package sampler;

import java.math.BigDecimal;

import org.oristool.simulator.samplers.Sampler;

/**
 * Sampler that always returns a constant value.
 */
public final class ConstantSampler implements Sampler {

    private final BigDecimal value;

    /**
     * Creates a constant sampler with the specified value.
     *
     * @param value the constant value to return
     */
    public ConstantSampler(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal getSample() {
        return this.value;
    }
}