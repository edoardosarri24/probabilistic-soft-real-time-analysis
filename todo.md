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
- negli appunti rifattorizzare con le basi e spiegare che anche in quelli normali possiamo shiftare il supporto.
- scrivere l'implementazione nel report.