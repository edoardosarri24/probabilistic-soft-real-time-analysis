# simulator
- Configurazione taskset con json o yaml.

### domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, ma magari ci interessa abortire il job e documentarlo in un qualche modo (come? cosa ci intreressa tracciare?)?
    - Quando si calcola la distribuzione dell'esecution time si deve anche considerare i tempi campionati dei job che hanno sforato la deadline? Supponendo che la simulazione non venga abortita appena si ha una deadline
- a questo punto la schedulazione di dataset non ci interessa. ci serve solo un singolo run di una data lunghezza?
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo? si testa ogni volta che si rilascia un job? e se troviamo che un job campiona un periodo più lungo della deadline?
    - devo ancora implementare controlli per verificare che un job al suo rilascio abbia la deadline assoluta minore del prossimo tempo del prossimo rilascio.
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa?

# Bernstein
- Calcolo della Derivata.
- implementare la classe Bersntein con i relavi test e documentare.

### migliorie
- basi
    - Per valori di $n$ molto elevati, il calcolo diretto Math.pow(x, i) * Math.pow(1-x, n-i) può portare a un underflow (risultato 0.0) anche se il valore finale sarebbe rappresentabile dopo la moltiplicazione per il coefficiente binomiale. Soluzione: Per calcoli ad alta precisione con $n$ grande, si potrebbe considerare il calcolo nello spazio logaritmico: exp(ln(binomial) + i*ln(x) + (n-i)*ln(1-x)).

### domande
- con la base esponenziale, deve essere $\lambda>0$ o $\lambda\ge0$?
    al momento è $\ge$, magari cambialo nell'implementazione.
- il fatto che la versione classica sia instabile anche da $n>20$ (è effettivamente così?) è un problema?