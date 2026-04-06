package config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.oristool.simulator.samplers.Sampler;
import sampler.DeterministicSampler;
import sampler.DiscreteChoiceSampler;

import java.math.BigDecimal;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SamplerConfig.DeterministicSamplerConfig.class, name = "deterministic"),
    @JsonSubTypes.Type(value = SamplerConfig.DiscreteChoiceSamplerConfig.class, name = "discreteChoice")
})

public abstract class SamplerConfig {

    public abstract Sampler toSampler();

    public static class DeterministicSamplerConfig extends SamplerConfig {
        public BigDecimal value;
        @Override
        public Sampler toSampler() {
            return new DeterministicSampler(value);
        }
    }

    public static class DiscreteChoiceSamplerConfig extends SamplerConfig {
        public List<BigDecimal> values;
        public List<Double> probabilities;
        @Override
        public Sampler toSampler() {
            return new DiscreteChoiceSampler(values, probabilities);
        }
    }

}
