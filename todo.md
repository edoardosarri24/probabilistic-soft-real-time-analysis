# simulator
- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, ma magari ci interessa abortire il job e documentarlo in un qualche modo? in che modo?
    - simulazione continua con due possibili scenari (settabili tramite strategy):
        -  I job che sforano la deadline vengano abortiti. Dobbiamo tenere traccia di alcune statistiche: numero assoluto di job che hanno abortito per task; percentuale di job che hanno abortito per task.
        - Si continua e si porta in fondo il job. Dobbiamo tenere traccia di alcune statistiche: TODO (controlla e vedi test.md).
- test dell'aborto.
- c'è un nome per le due configurazioni: abortisci il job e abortisci la simulazione.
- il nome abortedJob se la simulazine continua non va bene.
- Configurazione taskset con json o yaml.
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa? e in generale cosa vogliamo fare col simulatore? ci serve solo raccogliere distribuzioni?
    - servirà poi non importa farlo ora.


### domande
- Avevamo detto di mettere la deadline (relativa) stocastica campionata ad ogni realease del job.
    - Visto che abbiamo un fixed priority scheduler, questo è un problema.
    - Supponiamo quindi di tenerla deterministica. Quando avviene la realese del job $i$-esimo viene definita la sua deadline assoluta D (currentTime + relativeDeadline) e il tempo in cui avviene la release del job $i+1$-esimo R (currentTime + nextPeriodSampled). A questo punto ci sono due scenari:
        - $D\le R$: ok tutto regolare.
        - $D>R$: Il job $i$-esimo non avrà completato prima del rilascio del job $i+1$-esimo. Teoricamente non è un problema visto che il simulatore è stocastico e quindi è una situazione che è possibile. Praticamente non è un problema, ma è da implementare? È una situazione che ci interessa o in questo caso dobbiao porre $D=R$.

# Bernstein
- implementare la classe Bersntein con i relavi test e documentare? ci serve davvero una facade?
- L'approssimazione della CDF non l'ho implementata come nel in [questo](bernstein/paper/EPEW26_bernstein.pdf) paper. Ho cambiato base e usato la formula coeffic*base (vedi miei appunti).
- implementazione della derivata con la relativa documentazione.
- visulizzare anche la funzione originale?

### domande
