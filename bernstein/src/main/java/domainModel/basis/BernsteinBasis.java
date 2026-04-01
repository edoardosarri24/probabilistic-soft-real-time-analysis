package domainModel.basis;

import utils.MyMath;

/**
 * The subclasses must implement only the map method,
 * providing the appropriate clamping for the basis support.
 */
public abstract class BernsteinBasis {

    // Methods
    /**
     * Evaluates the i-th Bernstein basis polynomial of degree n at point x.
     */
    public final double eval(int degree, int i, double x) {
        double t = this.map(x);

        double binomialCoeff = MyMath.binomialCoefficient(degree, i);
        double tPower = Math.pow(t, i);
        double oneMinusTPower = Math.pow(1-t, degree-i);

        return binomialCoeff * tPower * oneMinusTPower;
    }

    // Hooks
    protected abstract double map(double x);


    public final double eval2(int degree, int i, double x) {
        double t = this.map(x);
        // Base cases optimization.
        if (t == 0.0)
            return (i == 0) ? 1.0 : 0.0;
        if (t == 1.0)
            return (i == degree) ? 1.0 : 0.0;
        // Binomial coefficient.
        double binomialCoeff = MyMath.binomialCoefficient(degree, i);
        if (binomialCoeff == 0.0)
            return 0.0;
        // Result
        double tPower = MyMath.intPow(t, i);
        double oneMinusTPower = MyMath.intPow(1.0-t, degree-i);
        return binomialCoeff * tPower * oneMinusTPower;
    }

}
