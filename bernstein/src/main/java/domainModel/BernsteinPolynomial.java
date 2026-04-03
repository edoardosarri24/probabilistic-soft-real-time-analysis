package domainModel;

import domainModel.basis.BernsteinBasis;
import utils.MyUtils;

public final class BernsteinPolynomial {

    private final double[] functionSampler;
    private final BernsteinBasis basis;

    // Constructor
    public BernsteinPolynomial(double[] functionSampler, BernsteinBasis basis) {
        this.functionSampler = functionSampler.clone();
        this.basis = basis;
    }

    // Methods.
    public double eval(double x) {
        // Degree chacks.
        int degree = functionSampler.length - 1;
        if (degree < 0)
            return 0.0;
        // Calculation.
        double result = 0.0;
        for (int i=0; i <= degree; i++) {
            double basis = this.basis.eval(degree, i, x);
            result += functionSampler[i] * basis;
        }
        return result;
    }

    public void visualize(double supportMin, double supportMax) {
        int degree = this.functionSampler.length-1;
        int numPoints = Math.max(200, degree * 10);

        double step = (supportMax - supportMin) / (numPoints - 1);
        double[] xValues = new double[numPoints];
        double[] yValues = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            xValues[i] = supportMin + i * step;
            yValues[i] = eval(xValues[i]);
        }
        MyUtils.callPythonVisualizer(xValues, yValues);
    }

}
