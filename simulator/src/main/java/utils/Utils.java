package utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Utils {

    /**
     * Generates a sorted list of unique {@link Duration} objects representing all multiples
     * of the given periods up to the specified simulation duration.
     *
     * <p>For each period in the input list, this method computes all its multiples (starting from the period itself)
     * that do not exceed the simulation duration. The resulting durations are collected, deduplicated, and sorted
     * in natural order.</p>
     *
     * @param periods            the list of periods as {@link Duration} objects; must not be null or empty
     * @param simulationDuration the maximum duration up to which multiples are generated; must not be null
     * @return a sorted {@link List} of unique {@link Duration} objects representing all multiples of the input periods up to the simulation duration
     * @throws IllegalArgumentException if the periods list is null or empty
     * @throws NullPointerException     if simulationDuration is null
     */
    public static List<Duration> generatePeriodUpToMax(List<Duration> periods, Duration simulationDuration) {
        if (periods == null || periods.isEmpty()) {
            throw new IllegalArgumentException("La lista dei periodi è vuota o nulla.");
        }
        Objects.requireNonNull(simulationDuration, "La durata della simulazione non può essere nulla.");

        long maxNanos = simulationDuration.toNanos();

        return periods.stream()
            .mapToLong(Duration::toNanos)
            .filter(baseNanos -> baseNanos > 0)
            .flatMap(baseNanos -> LongStream.iterate(baseNanos, m -> m <= maxNanos, m -> m + baseNanos))
            .distinct()
            .sorted()
            .mapToObj(Duration::ofNanos)
            .collect(Collectors.toCollection(ArrayList::new));
    }

}