package approximation;

import domainModel.ECDF;
import utils.MyMath;
import utils.MyUtils;

public final class BernsteinFromECDF {

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
        MyUtils.requireNonNull(ecdf, "ecdf");
        MyUtils.requirePositive(degree, "degree");
        MyUtils.validateRange(x, lowerSupport, upperSupport);
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
        MyUtils.requireNonNull(ecdf, "ecdf");
        MyUtils.requirePositive(degree, "degree");
        MyUtils.requireNonNegative(x, "x");
        // Result.
        double expMinusX = Math.exp(-x);
        double oneMinusExpMinusX = 1.0 - expMinusX;
        double result = 0.0;
        // Handle n=0.
        result += 1.0
            * MyMath.binomialCoefficient(degree, 0)
            * MyMath.intPow(oneMinusExpMinusX, degree);
        // Others iteration.
        for (int n=1; n <= degree; n++) {
            double empiricalCDF = ecdf.eval(Math.log((double) degree/n));
            double basis = MyMath.binomialCoefficient(degree, n)
                * MyMath.intPow(expMinusX, n)
                * MyMath.intPow(oneMinusExpMinusX, degree-n);
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
        MyUtils.requireNonNull(ecdf, "ecdf");
        MyUtils.requirePositive(degree, "degree");
        MyUtils.validateRange(x, lowerSupport, upperSupport);
        // Result.
        double result = 0.0;
        double range = upperSupport - lowerSupport;
        double previousEcdf = ecdf.eval(lowerSupport);
        for (int n=1; n <= degree; n++) {
            double currentEcdf = ecdf.eval(lowerSupport + (double)n/degree * range);
            double basis = MyMath.binomialCoefficient(degree, n)
                    * MyMath.intPow(x-lowerSupport, n-1)
                    * MyMath.intPow(upperSupport-x, degree-n)
                    / MyMath.intPow(range, degree - 1);
            result += (currentEcdf-previousEcdf) * n * basis;
            previousEcdf = currentEcdf;
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
        MyUtils.requireNonNull(ecdf, "ecdf");
        MyUtils.requirePositive(degree, "degree");
        MyUtils.requireNonNegative(x, "x");
        // Result.
        double result = 0.0;
        double expMinusX = Math.exp(-x);
        double oneMinusExpMinusX = 1.0 - expMinusX;
        // Handle n=1.
        double currentEcdf = ecdf.eval(Math.log(degree));
        double basis0 = MyMath.binomialCoefficient(degree, 1)
                * Math.exp(-x)
                * MyMath.intPow(oneMinusExpMinusX, degree-1);
        result += (1.0-currentEcdf) * 1 * basis0;
        // Others iteration.
        double previousEcdf = currentEcdf;
        for (int n=2; n <= degree; n++) {
            currentEcdf = ecdf.eval(Math.log((double) degree/n));
            double basis = MyMath.binomialCoefficient(degree, n)
                    * MyMath.intPow(expMinusX, n)
                    * MyMath.intPow(oneMinusExpMinusX, degree-n);
            result += (previousEcdf-currentEcdf) * n * basis;
            previousEcdf = currentEcdf;
        }
        return result;
    }

}
