# to do
- test con task peamente peridici e sporadici.

# valutare
- se due task hanno la stessa priorità allora il treeset dellos cheduelr che gestisce i job ne terrà solo uno.
- controllare che la deadline sia sempre più piccola del periodo.
- gestire il fatto di poter avere più task nei periodi futuri che non hanno completato in quello precedente? oppure se un task supera la deadline non abortire la simulazione ma eliminare il task.

# se c'è tempo
- Configurazione taskset con json o yaml.

# domande
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione.
- a questo punto la schedulazione di dataset non ci interessa. cioè una simulazione va avanti (al momento) finché non si verifica un deadline miss.
- al momento l'istante critico della simulazione è $t=0$.
- se il periodo è stocastico, come ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata? come ci assicuriamo di questo?