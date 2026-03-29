# simulator

### se c'è tempo
- Configurazione taskset con json o yaml.

### domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, magari ci interessa abortire il job e documentarlo in un qualche modo (come? cosa ci intreressa tracciare?)?
- a questo punto la schedulazione di dataset non ci interessa. ci serve solo un singolo run di una data lunghezza?
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo? si testa ogni volta che si rilascia un job? e se troviamo che un job campiona un periodo più lungo della deadline?
    - devo ancora implementare controlli per verificare che un job al suo rilascio abbia la deadline assoluta minore del prossimo tempo del prossimo rilascio.
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa?

# Bernstein
- scrivere l'implementazione delle basi nel report.
- Calcolo della Derivata
- viasualizzazione?

### migliorie
- Esiste una relazione di ricorrenza (e.g., algoritmo di de Casteljau) che permette di calcolare i polinomi di grado superiore partendo da quelli di grado inferiore e quindi in modo efficiente.
- ottimizzazione caching:
    Metodi come BernsteinOperator probabilmente chiameranno eval molte volte con lo stesso $n$ e $i$ diversi. Il coefficiente binomiale viene ricalcolato ogni volta. Se le performance dovessero diventare un collo di bottiglia, potresti introdurre una cache (es. una Map<Integer, BigInteger[]> o un triangolo di Pascal pre-calcolato).
- Per valori di $n$ molto elevati, il calcolo diretto Math.pow(x, i) * Math.pow(1-x, n-i) può portare a un underflow (risultato 0.0) anche se il valore finale sarebbe rappresentabile dopo la moltiplicazione per il coefficiente binomiale. Soluzione: Per calcoli ad alta precisione con $n$ grande, si potrebbe considerare il calcolo nello spazio logaritmico: exp(ln(binomial) + i*ln(x) + (n-i)*ln(1-x)).