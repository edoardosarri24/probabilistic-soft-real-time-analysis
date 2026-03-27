# generale
- file .gemini / .agent

# futuri scheduler
- direi che la classe scheduler è buona solo per scheduiling a priorità fissa. magari astraiamo.
- se due task hanno la stessa priorità allora il treeset dellos cheduelr che gestisce i job ne terrà solo uno.
- controllare che la deadline sia sempre più piccola del periodo.
- gestire il fatto di poter avere più task nei periodi futuri che non hanno completato in quello precedente? oppure se un task supera la deadline non abortire la simulazione ma eliminare il task.

# Task
- il periodo non deve essere una duration ma una distribuzione. gestire eventi: sono periodi.

# simulatore (alla fine)
- sistemare il readme in simulator/
    - mettere come utilizzare il simulatore.

# time collector
- definire
- documentare
- class diagram

# definizione taskset
Configurazione task con json o yaml

# no logging (prossimo)

# test
test con task peamente peridici e sporadici.

# domande
- cosa succede se un job sfora la deadline?
- a questo punto la schedulazione di dataset non ci interessa.
    In questro caso:
    - Rimuovere i metodi scheduleDataset e reset in Scheduler.
    - Modificare il MyClock.
- al momento l'istante critico della simulazione è $t=0$.