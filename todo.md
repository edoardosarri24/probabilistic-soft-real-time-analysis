# generale
- file .gemini / .agent

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