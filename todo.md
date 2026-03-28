# to do
- documetare no log.








- distribuzioni dei tempi di esecuzione del job dei task. forse in python? chiamare lo script python dal main java.
- test con task peamente peridici e sporadici.
- Controlli su parametri passati

# valutare
- se due task hanno la stessa priorità allora il treeset dellos cheduelr che gestisce i job ne terrà solo uno.
- controllare che la deadline sia sempre più piccola del periodo.
- gestire il fatto di poter avere più task nei periodi futuri che non hanno completato in quello precedente? oppure se un task supera la deadline non abortire la simulazione ma eliminare il task.

# se c'è tempo
- no logging.
- Configurazione taskset con json o yaml

# alla fine
- sistemare il readme in simulator/

# domande
- cosa succede se un job sfora la deadline?
- a questo punto la schedulazione di dataset non ci interessa.
    In questro caso:
    - Rimuovere i metodi scheduleDataset e reset in Scheduler.
    - Modificare il MyClock.
- al momento l'istante critico della simulazione è $t=0$.
- se il periodo è stocastico, cme ci asscuriamo che esso sia sempre minore della deadline, visto che questa è fissata?