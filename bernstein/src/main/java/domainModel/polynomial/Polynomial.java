package domainModel.polynomial;

import utils.MyUtils;

public abstract class Polynomial {

    private final double[] coefficients;

    // Constructor.
    /**
     * @param coefficients The coefficient that define the polynomial must be represented as: [c[0],c[1],c[2],...,c[n]].
     */
    public Polynomial(double[] coefficients) {
        MyUtils.requireNonNull(coefficients, "coefficients");
        if (coefficients.length == 0)
            throw new IllegalArgumentException("Polynomial must have at least one coefficient");
        this.coefficients = coefficients.clone();
    }

    // Getter and setter.
    public double[] getCoefficient() {
        return this.coefficients;
    }

    // Methods.
    public final double eval(double x) {
        int degree = this.coefficients.length - 1;
        double result = 0.0;
        for (int i=0; i <= degree; i++) {
            double basis = this.evalBasis(x, i, degree);
            result += this.coefficients[i] * basis;
        }
        return result;
    }

    // Hooks.
    protected abstract double evalBasis(double x, int i, int degree);

    /**
     * Convolve this polyunomial with another one dealing with the coefficients.
     */
    public abstract Polynomial convolve(Polynomial poly);

}
