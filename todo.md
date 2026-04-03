# simulator
- Configurazione taskset con json o yaml.
- capire cosa cambia tra activejobs e readyjobs.

- cosa succede se un job sfora la deadline? al momento si abortisce la simulazione, ma magari ci interessa abortire il job e documentarlo in un qualche modo? in che modo?
    - simulazione continua
    - execution time responsabile messo nelle statistiche.
    - prevedere due config dove in una i job che sforano la deadline vengano abortiti (tenere statistica di quanti job su tempo e sul totale) e nell'altra si continua e si porta in fondo il job.
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa? e in generale cosa vogliamo fare col simulatore? ci serve solo raccogliere distribuzioni?
    - servirà poi.

### domande
- Avevamo detto di mettere la deadline (relativa) stocastica campionata ad ogni realease del job.
    - Visto che abbiamo un fixed priority scheduler, questo è un problema.
    - Supponiamo quindi di tenerla deterministica. Quando avviene la realese del job $i$-esimo viene definita la sua deadline assoluta D (currentTime + relativeDeadline) e il tempo in cui avviene la release del job $i+1$-esimo R (currentTime + nextPeriodSampled). A questo punto ci sono due scenari:
        - $D\le R$: ok tutto regolare.
        - $D>R$: Il job $i$-esimo non avrà completato prima del rilascio del job $i+1$-esimo. Teoricamente non è un problema visto che il simulatore è stocastico e quindi è una situazione che è possibile. Praticamente non è un problema, ma è da implementare? È una situazione che ci interessa o in questo caso dobbiao porre $D=R$.

# Bernstein
- con la base esponenziale, deve essere $\lambda>0$ o $\lambda\ge0$?
    al momento è $\ge$, eventualmente cambialo nell'implementazione.
- La convuluzione serve solo a sommare distribuzioni? Perché la carnevali ne parlava per polinomi di Bernstein?
- implementazione della derivata con la relativa documentazione.
- implementare la classe Bersntein con i relavi test e documentare? ci serve davvero una facade?
- fare la visualizzazione.

### domande
- vedi sezione 2.3 de report.
- ci interessa solo la derivata pria giusto? cioè passare dall'approssimazione della cdf a quella della pdf?
- nella valutazione del polinomio (non derivata, quindi quello che approssimerà la cdf) nelle formule del paper ci sono formule diverse rispetto ad usare $\sum coeff*base$, dove base cambia a seconda se è classica, lineare o esponenziale. è la stessa cosa oppure no?
- nell'implementazione si vuole usare un array di coefficienti. nella formule questi correispondono alla funzione f(i/n). nelle derivate abbiamo la differenza tra due coefficienti consecutivi giusto?

# paper
- primo rosso sottolineato nell'articolo.
- dove c'è la y, si intende la x all'interno di quell'intervallo specifico giusto?
- come si mette la lambda nelle formule? nella base ce l'ho messa quindi mi immagino devo metterla anche nell'approssimazione della cdf e pdf.