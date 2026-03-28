# Relazione di Test - Simulatore

## Panoramica
La strategia di testing per il simulatore di real-time probabilistico è stata progettata per garantire sia la correttezza logica dell'algoritmo di scheduling che la robustezza del motore di simulazione ad eventi discreti (DES). I test sono divisi in due categorie principali: **System Test Deterministici** e **Robustness Test Stocastici**.

## 1. System Test Deterministici (`DMSchedulerSystemTest.java`)
Questi test utilizzano il `ConstantSampler` per eliminare l'incertezza e permettere una verifica puntuale del comportamento dello scheduler rispetto a scenari teorici noti.

### Scenari Testati
- **Esecuzione Task Singolo:** Garantisce che il simulatore gestisca correttamente il rilascio periodico e il completamento dei job. Verifica che il numero di campioni raccolti corrisponda esattamente alle release attese (inclusi i punti di confine come $t=0$ e $t=T_{sim}$).
- **Preemption e Priorità DM:** Valida il cuore del `DMScheduler`. Verifica che le priorità siano assegnate correttamente in base alle deadline (Deadline Monotonic) e che un task a priorità alta interrompa (preempt) un task a priorità bassa nel momento esatto del rilascio.
- **Gestione Overload:** Verifica che il sistema rilevi immediatamente un superamento della deadline e sollevi correttamente la `DeadlineMissedException` quando il carico richiesto è superiore alle capacità del processore (U > 100%).

### Cosa Garantiscono
- La correttezza dell'algoritmo di assegnazione delle priorità.
- La precisione del clock di simulazione (`MyClock`) e della coda degli eventi.
- La corretta gestione degli stati dei Job (Ready, Executing, Completed).

## 2. Robustness Test Stocastici (`StochasticRobustnessTest.java`)
Questi test utilizzano distribuzioni di probabilità reali (`UniformSampler`, `ExponentialSampler`) e parametri casuali per mettere sotto sforzo il motore di simulazione.

### Scenari Testati
- **Mix Stocastico Multi-Task:** Esegue simulazioni ripetute con jitter e tempi di esecuzione variabili per assicurarsi che non si verifichino errori interni (come `NullPointerException` o loop infiniti) dovuti a sovrapposizioni impreviste di eventi.
- **Stress Test ad Alta Densità:** Simula set di molti task (15+) con periodi molto brevi e casuali. Questo scenario è critico per verificare la tenuta della `PriorityQueue` degli eventi e la stabilità numerica delle operazioni su `java.time.Duration`.

### Cosa Garantiscono
- La stabilità del motore DES contro eventi "quasi simultanei" o micro-jitter.
- L'assenza di memory leak o deadlock durante l'elaborazione di migliaia di eventi stocastici.
- La robustezza dell'integrazione con la libreria esterna Sirio per il campionamento.

## Framework e Strumenti
- **JUnit 5 (Jupiter):** Utilizzato per la sua moderna gestione delle eccezioni (`assertThrows`) e per i `@RepeatedTest`, essenziali per il testing stocastico.
- **AssertJ:** Utilizzato per scrivere asserzioni fluide e leggibili, migliorando la manutenibilità dei test.
- **Automazione:** Tutti i test sono integrati nello script `./exec/simulator.sh`, garantendo che ogni modifica al codice venga validata automaticamente.
