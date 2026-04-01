# paper domande
- primo rosso sottolineato nell'articolo.
- dove c'è la y, si intende la x all'interno di quell'intervallo specifico giusto?
- come si mette la lambda nelle formule? nella base ce l'ho messa quindi mi immagino devo metterla anche nell'approssimazione della cdf e pdf.

# simulator
- Configurazione taskset con json o yaml.

### domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, ma magari ci interessa abortire il job e documentarlo in un qualche modo (come? cosa ci intreressa tracciare?)?
- Quando si calcola la distribuzione dell'esecution time si deve anche considerare i tempi campionati dei job che hanno sforato la deadline? Supponendo che la simulazione non venga abortita appena si ha una deadline.
- a questo punto la schedulazione di dataset non ci interessa. ci serve solo un singolo run di una data lunghezza?
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo? si testa ogni volta che si rilascia un job? e se troviamo che un job campiona un periodo più lungo della deadline?
    - devo ancora implementare controlli per verificare che un job al suo rilascio abbia la deadline assoluta minore del prossimo tempo del prossimo rilascio.
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa?

# Bernstein
- implementazione della derivata con la relativa documentazione.
- implementare la classe Bersntein con i relavi test e documentare? ci serve davvero una facade?

### domande
- con la base esponenziale, deve essere $\lambda>0$ o $\lambda\ge0$?
    al momento è $\ge$, magari cambialo nell'implementazione.
- vedi sezione 2.3 de report.
- il fatto che la versione classica sia instabile anche da $n>20$ (è effettivamente così?) è un problema?
- come si implementano quando con i coefficienti i valori dentro la funzione nelle formule?

# varie
- La convuluzione serve solo a sommare distribuzioni? Perché la carnevali ne parlava per polinomi di Bernstein?