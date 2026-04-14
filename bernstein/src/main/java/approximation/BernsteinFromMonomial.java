package approximation;
import domainModel.polynomial.BernsteinPolynomial;
import domainModel.polynomial.MonomialPolynomial;
import domainModel.polynomial.bernsteinBasis.LinearBernsteinbasis;
import utils.MyMath;
import utils.MyUtils;

public final class BernsteinFromMonomial {

    /**
     * Exact conversion from monomial coefficients to Bernstein ones on [0,1] for N=M using closed-form formula.
     */
    public static BernsteinPolynomial withDirectCoefficientConversion(MonomialPolynomial polynomial) {
        MyUtils.requireNonNull(polynomial, "polynomial");
        double[] oldCoefficients = polynomial.getCoefficient();
        int degree = oldCoefficients.length - 1;
        double[] newCoefficients = new double[degree+1];
        for(int i=0 ; i < degree+1 ; i++) {
            double newCoefficient = 0.0;
            for(int j=0 ; j <= i ; j++)
                newCoefficient += MyMath.binomialCoefficient(i,j)
                    / MyMath.binomialCoefficient(degree,j)
                    * oldCoefficients[j];
            newCoefficients[i] = newCoefficient;
        }
        return new BernsteinPolynomial(newCoefficients, new LinearBernsteinbasis(0.0, 1.0));
    }

    /**
     * Conversion from monomial basis to Bernstein basis on [0,1] for N=M using matrix inversion.
     */
    public static BernsteinPolynomial withMatrixInversion(MonomialPolynomial polynomial) {
        MyUtils.requireNonNull(polynomial, "polynomial");
        double[] oldCoefficients = polynomial.getCoefficient();
        int degree = oldCoefficients.length - 1;
        // Define the matrix.
        double[][] matrix = new double[degree+1][degree+1];
        for (int i=0; i <= degree; i++)
            for (int j=i; j <= degree; j++)
                matrix[j][i] = MyMath.binomialCoefficient(degree, i)
                        * MyMath.binomialCoefficient(degree-i, j-i)
                        * ((j - i) % 2 == 0 ? 1.0 : -1.0);
        // Solving it.
        double[] newCoefficients = new double[degree + 1];
        for (int i=0; i <= degree; i++) {
            double sum = 0.0;
            for (int j=0; j < i; j++)
                sum += matrix[i][j] * newCoefficients[j];
            newCoefficients[i] = (oldCoefficients[i] - sum) / matrix[i][i];
        }
        // Return.
        return new BernsteinPolynomial(newCoefficients, new LinearBernsteinbasis(0.0, 1.0));
    }

}

