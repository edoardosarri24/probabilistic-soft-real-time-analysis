# to do
- constantsampler deve diventare determinsitciSamper.

# valutare
- se due task hanno la stessa priorità allora il treeset dellos cheduelr che gestisce i job ne terrà solo uno.

# se c'è tempo
- Configurazione taskset con json o yaml.

# domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione.
- a questo punto la schedulazione di dataset non ci interessa. cioè una simulazione va avanti (al momento) finché non si verifica un deadline miss.
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo?
    - devo ancora implementare controlli perché un job al suo rilascio abbia la deadline assoluta maggiore del prossimo tempo del prossimo rilascio.
- al momento l'istante critico della simulazione è $t=0$.