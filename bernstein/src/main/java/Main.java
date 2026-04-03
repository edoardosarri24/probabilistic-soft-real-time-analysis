import java.math.BigDecimal;

import javax.sound.sampled.Line;

import org.oristool.simulator.samplers.ExponentialSampler;

import domainModel.BernsteinPolynomial;
import domainModel.basis.BernsteinBasis;
import domainModel.basis.ClassicalBernsteinBasis;
import domainModel.basis.ExponentialBernsteinBasis;
import domainModel.basis.LinearBernsteinBasis;

/**
 * Hello world!
 */
public class Main {
    public static void main(String[] args) {
        // 1. Definiamo il grado del polinomio (n)
        // Più alto è il grado, migliore sarà l'approssimazione
        int degree = 10; 

        // 2. Creiamo il campionatore della funzione (functionSampler)
        // I punti di campionamento per Bernstein sono uniformi: k/n
        double[] sampler = new double[degree + 1];
        for (int i = 0; i <= degree; i++) {
            double x_i = (double) i / degree;
            new ExponentialSampler(new BigDecimal(10));
            sampler[i] = Math.cos(Math.PI * x_i);
        }

        // 3. Istanziamo la base e il polinomio
        BernsteinBasis basis = new LinearBernsteinBasis(0, 1);
        BernsteinPolynomial poly = new BernsteinPolynomial(sampler, basis);

        // Visualizziamo su 20 punti tra 0 e 1 per vedere la curva risultante
        poly.visualize(0.0, 1.0, null);
    }

}
