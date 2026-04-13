import approximation.BernsteinFromMonomial;
import polynomial.BernsteinPolynomial;
import polynomial.MonomialPolynomial;
import polynomial.PolynomialDistance;

/**
 * P(x) = 1 + x + x^2 + ... + x^d with d={10, 50, 100, 1000}
 */
public class Main {
    public static void main(String[] args) {
        int[] degrees = {10, 25, 35, 40, 50, 100, 1029, 1030, 1100};
        for (int degree : degrees) {
            double[] coefficients = new double[degree+1];
            for (int i=0; i <= degree; i++)
                coefficients[i] = 1.0;
            MonomialPolynomial poly = new MonomialPolynomial(coefficients);

            // Direct
            BernsteinPolynomial bernsteinDirect = BernsteinFromMonomial.withDirectCoefficientConversion(poly);
            PolynomialDistance.withPlot("sum_until_d_" + degree + "_Direct", poly, bernsteinDirect, 0.0, 1.0, 100);

            // Matrix
            BernsteinPolynomial bernsteinMatrix = BernsteinFromMonomial.withMatrixInversion(poly);
            PolynomialDistance.withPlot("sum_until_d_" + degree + "_Matrix", poly, bernsteinMatrix, 0.0, 1.0, 100);
        }
    }

}
