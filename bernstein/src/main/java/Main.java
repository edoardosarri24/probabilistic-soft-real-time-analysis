import polynomial.MonomialPolynomial;
import polynomial.PolynomialDistance;

public class Main {
    public static void main(String[] args) {
        // Test Polynomial.visualyze with dynamic naming
        MonomialPolynomial poly1 = new MonomialPolynomial(new double[]{0, 0, 1}); // x^2
        poly1.visualyze("x_squared", 0, 1, 100);
        System.out.println("Generated: results/polynomia_visualization_x_squared.pdf");

        // Test PolynomialDistance.withPlot with dynamic naming
        MonomialPolynomial poly2 = new MonomialPolynomial(new double[]{0, 1});    // x
        System.out.println("Calculating distance between x^2 and x...");
        PolynomialDistance.withPlot("comparison_x2_x", poly1, poly2, 0, 1, 100);
        System.out.println("Generated: results/polynomial_distance_visualization_comparison_x2_x.pdf");
    }
}
