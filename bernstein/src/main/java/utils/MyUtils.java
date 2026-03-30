package utils;

import java.util.Objects;

public final class MyUtils {

    // Constructor.
    private MyUtils() {}

    // Validation Methods.
    public static <T> T requireNonNull(T obj, String paramName) {
        return Objects.requireNonNull(obj, paramName + " cannot be null");
    }

    public static double requirePositive(double value, String paramName) {
        if (value <= 0)
            throw new IllegalArgumentException(paramName + " must be positive");
        return value;
    }

    public static double requireNonNegative(double value, String paramName) {
        if (value < 0)
            throw new IllegalArgumentException(paramName + " cannot be negative");
        return value;
    }

}
