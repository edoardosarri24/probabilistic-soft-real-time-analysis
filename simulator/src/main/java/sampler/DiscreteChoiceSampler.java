package sampler;

import org.oristool.simulator.samplers.Sampler;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Sampler that selects a value from a discrete set with associated probabilities.
 */
public class DiscreteChoiceSampler implements Sampler {

    private final List<BigDecimal> values;
    private final double[] cumulativeProbabilities;

    /**
     * Creates a discrete choice sampler with the given values and probabilities.
     *
     * @param values list of values to sample from
     * @param probabilities list of probabilities for each value (must sum to 1.0)
     * @throws IllegalArgumentException if lists have different sizes or probabilities don't sum to ~1.0
     */
    public DiscreteChoiceSampler(List<BigDecimal> values, List<Double> probabilities) {
        // Input checks
        if (values == null || probabilities == null || values.size() != probabilities.size())
            throw new IllegalArgumentException("Values and probabilities must have the same length and not be null");

        if (values.isEmpty())
            throw new IllegalArgumentException("Values and probabilities cannot be empty");

        double sum = 0.0;
        for (double p : probabilities) {
            if (p < 0.0 || p > 1.0)
                throw new IllegalArgumentException("Probabilities must be between 0 and 1");
            sum += p;
        }
        if (Math.abs(sum - 1.0) > 1e-6)
            throw new IllegalArgumentException("Probabilities must sum to 1.0, but sum is " + sum);

        // Defensive copy of the list to ensure immutability
        this.values = List.copyOf(values);

        // Build cumulative probability array for efficient binary search
        this.cumulativeProbabilities = new double[probabilities.size()];
        double cumulative = 0.0;
        for (int i = 0; i < probabilities.size(); i++) {
            cumulative += probabilities.get(i);
            this.cumulativeProbabilities[i] = cumulative;
        }

        // Ensure the last entry is exactly 1.0 to handle precision issues during search
        this.cumulativeProbabilities[this.cumulativeProbabilities.length - 1] = 1.0;
    }

    @Override
    public BigDecimal getSample() {
        double randomValue = Math.random();
        int index = Arrays.binarySearch(cumulativeProbabilities, randomValue);
        if (index < 0)
            index = -(index + 1);
        return values.get(Math.min(index, values.size() - 1));
    }
}