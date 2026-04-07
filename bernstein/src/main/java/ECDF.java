import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents an Empirical Comulative Distribution Function.
 */
public class ECDF {

    private final double[] data;

    // Constructor.
    /**
     * @param inputData La collezione di dati in input.
     * @throws IllegalArgumentException se l'input o un suo elemento è null.
     */
    public ECDF(Collection<Double> inputData) {
        // Input checks.
        if (inputData == null)
            throw new IllegalArgumentException("I dati in input non possono essere null");
        // Memorize and sort the input.
        this.data = new double[inputData.size()];
        int i = 0;
        for (Double data : inputData) {
            if (data == null)
                throw new IllegalArgumentException("I valori nulli non sono supportati");
            this.data[i++] = data;
        }
        Arrays.sort(this.data);
    }

    // Methods.
    /**
     * Returs the {@code P(X <= x)}.
     * @param x The theshold.
     * @return The empirical probability.
     */
    public double eval(double x) {
        // Base case.
        if (this.data.length == 0)
            return 0.0;
        // Result.
        int count = count(x);
        return (double) count / this.data.length;
    }

    // Helper.
    /**
     * Conta quanti elementi nell'insieme di dati sono minori o uguali a x.
     * Utilizza la ricerca binaria in tempo O(log n).
     * * @param x Il valore di soglia.
     * @return Il numero intero di elementi <= x.
     */
    private int count(double x) {
        // Base case.
        if (this.data.length == 0)
            return 0;
        // Result.
        int index = Arrays.binarySearch(data, x);
        if (index < 0) {
            index = -(index + 1);
        } else {
            // Handle duplicate.
            while (index < this.data.length-1 && data[index+1] == x)
                index++;
            index++;
        }
        return index;
    }

}