import utils.MyMath;

public final class Bernstein {

    /**
     * Approximates the ECDF using the standard Bernstein basis on [0,1].
     * @param ecdf The empirical CDF to approximate.
     * @param x The value at which to evaluate the approximation (must be in [0,1]).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @return The approximated CDF value at x.
     */
    public static double approximateECDFWithNormalBase(ECDF ecdf, double x, int degree) {
        return approximateECDFWithLinearBase(ecdf, x, degree, 0.0, 1.0);
    }

    /**
     * Approximates the ECDF using a linear Bernstein basis for a compact support [a,b].
     * @param ecdf The empirical CDF to approximate.
     * @param x The value at which to evaluate the approximation (must be in [lowerSupport, upperSupport]).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @param lowerSupport The lower bound of the support (a).
     * @param upperSupport The upper bound of the support (b).
     * @return The approximated CDF value at x.
     */
    public static double approximateECDFWithLinearBase(ECDF ecdf, double x, int degree, double lowerSupport, double upperSupport) {
        // Input checks.
        validateInputs(ecdf, degree);
        validateRange(x, lowerSupport, upperSupport);
        // Result.
        double result = 0.0;
        double range = upperSupport - lowerSupport;
        for (int n=0; n <= degree; n++) {
            double empiricalCDF = ecdf.eval(lowerSupport + (double)n/degree * range);
            double basis = MyMath.binomialCoefficient(degree, n)
                * MyMath.intPow(x-lowerSupport, n)
                * MyMath.intPow(upperSupport-x, degree-n)
                / MyMath.intPow(range, degree);
            result += empiricalCDF * basis;
        }
        return result;
    }

    /**
     * Approximates the ECDF using an exponential Bernstein basis (Bernstein Phase Type) for semi-infinite support [0,inf).
     *
     * @param ecdf The empirical CDF to approximate.
     * @param x The value at which to evaluate the approximation (x >= 0).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @return The approximated CDF value at x.
     */
    public static double approximateECDFWithExponentialBase(ECDF ecdf, double x, int degree) {
        // Input checks.
        validateInputs(ecdf, degree);
        if (x < 0)
            throw new IllegalArgumentException("The value of x must be [0,inf].");
        // Result.
        double result = 0.0;
        // Handle n=0.
        result += 1.0
            * MyMath.binomialCoefficient(degree, 0)
            * MyMath.intPow(1 - Math.exp(-x), degree);
        // Others iteration.
        for (int n=1; n <= degree; n++) {
            double empiricalCDF = ecdf.eval(Math.log((double) degree/n));
            double basis = MyMath.binomialCoefficient(degree, n)
                * Math.exp(-n * x)
                * MyMath.intPow(1-Math.exp(-x), degree-n);
            result += empiricalCDF * basis;
        }
        return result;
    }

    /**
     * Approximates the Empirical Probability Density Function (EPDF) using the derivative of the Bernstein polynomial on [0,1].
     * @param ecdf The empirical CDF used for the derivative calculation.
     * @param x The value at which to evaluate the density (must be in [0,1]).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @return The approximated PDF value at x.
     */
    public static double approximateEPDFWithNormalBase(ECDF ecdf, double x, int degree) {
        return approximateEPDFWithLinearBase(ecdf, x, degree, 0.0, 1.0);
    }

    /**
     * Approximates the EPDF using the derivative of the linear Bernstein basis for a compact support [a,b].
     * @param ecdf The empirical CDF used for the derivative calculation.
     * @param x The value at which to evaluate the density (must be in [lowerSupport, upperSupport]).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @param lowerSupport The lower bound of the support (a).
     * @param upperSupport The upper bound of the support (b).
     * @return The approximated PDF value at x.
     */
    public static double approximateEPDFWithLinearBase(ECDF ecdf, double x, int degree, double lowerSupport, double upperSupport) {
        // Input checks.
        validateInputs(ecdf, degree);
        validateRange(x, lowerSupport, upperSupport);
        // Result.
        double result = 0.0;
        double range = upperSupport - lowerSupport;
        for (int n=1; n <= degree; n++) {
            double firstEmpiricalCDF = ecdf.eval(lowerSupport + (double)n/degree * range);
            double secondEmpiricalCDF = ecdf.eval(lowerSupport + (double)(n-1) / degree * range);
            double basis = MyMath.binomialCoefficient(degree, n)
                    * MyMath.intPow(x-lowerSupport, n-1)
                    * MyMath.intPow(upperSupport-x, degree-n)
                    / MyMath.intPow(range, degree - 1);
            result += (firstEmpiricalCDF-secondEmpiricalCDF) * n * basis;
        }
        return result/range;
    }

    /**
     * Approximates the EPDF using the derivative of the exponential Bernstein basis (Bernstein Phase Type).
     * @param ecdf   The empirical CDF used for the derivative calculation.
     * @param x      The value at which to evaluate the density (x >= 0).
     * @param degree The actual degree of the approximating Bernstein polynomial.
     * @return The approximated PDF value at x.
     */
    public static double approximateEPDFWithExponentialBase(ECDF ecdf, double x, int degree) {
        // Input checks.
        validateInputs(ecdf, degree);
        if (x < 0)
            throw new IllegalArgumentException("The value of x must be [0,inf].");
        // Result.
        double result = 0.0;
        // Handle n=1.
        double empiricalCDF1 = ecdf.eval(Math.log((double) degree / 1));
        double basis1 = MyMath.binomialCoefficient(degree, 1)
                * Math.exp(-x)
                * MyMath.intPow(1-Math.exp(-x), degree-1);
        result += (1.0-empiricalCDF1) * 1 * basis1;
        // Others iteration.
        for (int n=2; n <= degree; n++) {
            double firstEmpiricalCDF = ecdf.eval(Math.log((double) degree / (n - 1)));
            double secondEmpiricalCDF = ecdf.eval(Math.log((double) degree/n));
            double basis = MyMath.binomialCoefficient(degree, n)
                    * Math.exp(-n * x)
                    * MyMath.intPow(1-Math.exp(-x), degree-n);
            result += (firstEmpiricalCDF-secondEmpiricalCDF) * n * basis;
        }
        return result;
    }

    // Helper
    private static void validateInputs(ECDF ecdf, int degree) {
        if (ecdf == null)
            throw new IllegalArgumentException("The ECDF object must be non-null.");
        if (degree <= 0)
            throw new IllegalArgumentException("The polynomial degree must be grether than zero. Now it's: " + degree);
    }

    private static void validateRange(double x, double lower, double upper) {
        if (x < lower || x > upper)
            throw new IllegalArgumentException(
                String.format("The value of x si out of the support range: %f isn't [%f, %f]", x, lower, upper)
            );
        if (lower >= upper)
            throw new IllegalArgumentException(
                String.format("The support is invalid: lower bound (%f) must be lower than the supper (%f)", lower, upper)
            );
    }

}
