# simulator
- trovare anche il valore $\alpha_{k,t}$ che rappresneta il numero di job di ogni task che fa interferenza sul task a probabilità più bassa? e in generale cosa vogliamo fare col simulatore? ci serve solo raccogliere distribuzioni?

### domande
- Avevamo detto di mettere la deadline (relativa) stocastica campionata ad ogni realease del job.
    - Visto che abbiamo un fixed priority scheduler, questo è un problema.
    - Supponiamo quindi di tenerla deterministica. Quando avviene la realese del job $i$-esimo viene definita la sua deadline assoluta D (currentTime + relativeDeadline) e il tempo in cui avviene la release del job $i+1$-esimo R (currentTime + nextPeriodSampled). A questo punto ci sono due scenari:
        - $D\le R$: ok tutto regolare.
        - $D>R$: Il job $i$-esimo non avrà completato prima del rilascio del job $i+1$-esimo. Teoricamente non è un problema visto che il simulatore è stocastico e quindi è una situazione che è possibile. Praticamente non è un problema, ma è da implementare? È una situazione che ci interessa o in questo caso dobbiao porre $D=R$.

# Bernstein
- mettere i test di Bernstein nella documentazione.
- sistema (MyUtils) nel vecchio bernstein e sistema la documentazione. capire quello che mi serve.
- ultimo incontro con la carnevali.
- visulizzazione?
- quando calcola le basi lineari, posso anche calcolare la base in [0,1] e fare il cambio di variabile? non è più efficiente e meno rischio di overflow?

### domande