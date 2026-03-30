package domainModel.basis;

import java.math.BigInteger;

import utils.MyMath;

/**
 * The subclasses must implement only the clampo method, providing the appropriate clamping for the support of the basis.
 */
public abstract class BernsteinBasis {

    // Methods
    /**
     * Evaluates the i-th Bernstein basis polynomial of degree n at point x.
     */
    public final double eval(int i, int n, double x) {
        double t = this.clamp(x);
        BigInteger binomialCoeff = MyMath.binomialCoefficient(n, i);
        double tPower = Math.pow(t, i);
        double oneMinusTPower = Math.pow(1-t, n-i);
        return binomialCoeff.doubleValue() * tPower * oneMinusTPower;
    }

    // Hooks
    public abstract double clamp(double x);

}
