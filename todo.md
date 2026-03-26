# Task
- chiarire cosa e quando sono utilizzati i metodi purelyPeriodicCheck, utilizationFactor e periodAndDealineCheck.
- il periodo non deve essere una duration ma una distribuzione.

# Taskset
- copire a cosa serve e quando viene usato purelyPeriodicCheck, periodAndDealineCheck, hyperbolicBoundTest e utilizationFactor.

# Scheduler
- In DM capire cosa è e quando è usato checkFeasibility.

# Sampler
- ho implementato il constant sampler. docuementarlo nella doc e nel class diagram.

# Simulator
- sistemare il readme in simulator/
- documentare come utilizzare il simulatore.
- documentare il logging.
- documentare il rilevamento delle deadline.

# codice
- aggiugere controlli?

# domande
- cosa succede se un job sfora la deadline?
- a questo punto la schedulazione di dataset non ci interessa.
    In questro caso:
    - Rimuovere i metodi scheduleDataset e reset in Scheduler.
    - Modificare il MyClock.
- al momento l'istante critico della simulazione è $t=0$.