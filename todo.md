# simulator
- al momento l'istante critico della simulazione è $t=0$. implementare un qualcosa che mi permetta di sportare l'istante critico dei task.
- Choice sampler

### se c'è tempo
- Configurazione taskset con json o yaml.

### domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, magari ci interessa abortire il job e documentarlo in un qualche modo (come? cosa ci intreressa tracciare?)?
- a questo punto la schedulazione di dataset non ci interessa. ci serve solo un singolo run di una data lunghezza?
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo? si testa ogni volta che si rilascia un job? e se troviamo che un job campiona un periodo più lungo della deadline?
    - devo ancora implementare controlli per verificare che un job al suo rilascio abbia la deadline assoluta minore del prossimo tempo del prossimo rilascio.

# Bernstein