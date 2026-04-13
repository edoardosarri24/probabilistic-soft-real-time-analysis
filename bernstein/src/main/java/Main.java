import polynomial.MonomialPolynomial;

public class Main {
    public static void main(String[] args) {
        MonomialPolynomial poly = new MonomialPolynomial(new double[]{0, 0, 1, 4, 6, 10});
        poly.visualyze("example", 0, 1, 5);
        System.out.println("Done! Check results/polynomial.pdf");
    }
}