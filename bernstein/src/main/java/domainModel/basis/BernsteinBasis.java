package domainModel.basis;

import utils.MyMath;

/**
 * The subclasses must implement only the clampo method, providing the appropriate clamping for the support of the basis.
 */
public abstract class BernsteinBasis {

    // Methods
    /**
     * Evaluates the i-th Bernstein basis polynomial of degree n at point x.
     */
    public final double eval(int i, int degree, double x) {
        double t = this.map(x);
        double binomialCoeff = MyMath.binomialCoefficient(degree, i).doubleValue();
        double tPower = Math.pow(t, i);
        double oneMinusTPower = Math.pow(1-t, degree-i);
        return binomialCoeff * tPower * oneMinusTPower;
    }

    // Hooks
    protected abstract double map(double x);

}
