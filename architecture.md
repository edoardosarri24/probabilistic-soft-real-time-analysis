
## 1. Modello dei Dati: Task e Job

### Job.java (L'Istanza di Esecuzione)
- **Timeline Dinamica**: Ogni Job nasce con un `releaseTime` (l'istante attuale del simulatore) e una `absoluteDeadline` calcolata come `releaseTime + task.getDeadline()`.
- **Completion Tracking**: Aggiunto il campo `completionTime`. Questo è vitale perché, in un sistema stocastico, un job può completarsi in qualsiasi istante. Senza questo dato, non potremmo sapere se un job finito è stato puntuale o meno dopo che il tempo è avanzato.

---

## 2. Architettura DES: La Gestione degli Eventi

Il simulatore non avanza più per "piccoli passi" costanti (time-stepping), ma "salta" tra istanti critici chiamati **Eventi**.

### La Gerarchia degli Eventi
È stata introdotta una struttura a classi nel pacchetto `event` per tipizzare gli eventi:
1.  **`Event` (Base)**: Classe astratta che definisce il timestamp (`time`).
2.  **`ReleaseEvent`**: Indica che un nuovo Job deve essere creato e inserito nel sistema.
3.  **`DeadlineEvent`**: Un "segnaposto" temporale. Forza lo scheduler a fermarsi all'istante esatto della scadenza di un job per verificarne lo stato.

### Struttura Dati Core: `eventQueue`
Tutti gli eventi sono mantenuti in una `PriorityQueue<Event>`. Grazie al metodo `compareTo`, la coda garantisce che:
- Gli eventi siano estratti in ordine cronologico.
- In caso di simultaneità, i **`DeadlineEvent` vengano estratti prima dei `ReleaseEvent`**. Questo tie-breaking è fondamentale per validare lo stato di un job in scadenza prima che il rilascio successivo dello stesso task possa sovrascriverlo in `activeJobs`.

---

## 3. Flusso Implementativo dello Scheduler

Lo `Scheduler` gestisce tre strutture dati principali:
- `eventQueue (PriorityQueue)`: Gli eventi futuri (Rilasci e Deadline).
- `readyJobs (TreeSet)`: I job pronti a eseguire, ordinati per priorità.
- `activeJobs (HashMap<Task, Job>)`: Il job attualmente "in vita" per ogni task (usato per il controllo deadline).

### Fase 1: Inizializzazione (`resetState`)
1.  Pulisce tutte le mappe e le code.
2.  Resetta il `MyClock` a zero.
3.  Pianifica un `ReleaseEvent` iniziale a $t=0$ per ogni task presente nel `TaskSet`.

### Fase 2: Il Ciclo di Analisi (`analyze`)
Per ogni evento prelevato dalla `eventQueue`:
1.  **Consumo del tempo**: Calcola `availableTime = nextEventTime - clock.getCurrentTime()`. Chiama `distributeAvailableTime(availableTime)`.
2.  **Sincronizzazione Clock**: Chiama `clock.advanceTo(nextEventTime)`. Ora il simulatore è ufficialmente all'istante dell'evento.
3.  **Batching Contemporaneo**: Con un ciclo `while`, preleva dalla coda tutti gli altri eventi che hanno lo stesso identico timestamp.
4.  **Controllo Deadline**: Esegue `checkDeadlines()` su tutti i job presenti in `activeJobs`. Questa è la "barriera" di sicurezza: se un job ha superato la sua `absoluteDeadline`, la simulazione si interrompe con un'eccezione.
5.  **Esecuzione Rilasci**: Per ogni `ReleaseEvent` nel batch:
    -   Chiama `task.releaseJob()` per creare il nuovo Job.
    -   Lo inserisce in `activeJobs` (sovrascrivendo il precedente) e in `readyJobs`.
    -   Inserisce un nuovo `DeadlineEvent` nella `eventQueue` all'istante `newJob.getAbsoluteDeadline()`.
    -   Campiona il prossimo periodo e inserisce il prossimo `ReleaseEvent`.

### Fase 3: Distribuzione del Tempo (`distributeAvailableTime`)
Questa funzione gestisce l'esecuzione effettiva tra due eventi:
```java
while (availableTime > 0 && !readyJobs.isEmpty()) {
    Job highPriorityJob = readyJobs.pollFirst(); // Prende il top priority
    
    // Gestione Preemption: se il job è diverso dall'ultimo eseguito, logga "preempt"
    
    // Calcola quanto può eseguire: min(availableTime, remainingExecutionTime)
    Duration timeToExecute = ... 
    Duration executedTime = highPriorityJob.execute(timeToExecute);
    
    clock.advanceBy(executedTime); // Avanza il clock reale
    
    if (highPriorityJob.isCompleted()) {
        highPriorityJob.setCompletionTime(clock.getCurrentTime());
    }
    
    checkDeadlines(); // Verifica immediata post-esecuzione
    
    availableTime -= executedTime;
    if (!highPriorityJob.isCompleted()) {
        readyJobs.add(highPriorityJob); // Reinserisce se non ha finito
    }
}
```

---

## 4. Soluzione al Bug delle Deadline (Logica Post-Mortem)

Nel vecchio simulatore deterministico, il controllo era approssimativo. Nella nuova architettura DES, la sicurezza è doppia:
1.  **Fermata Deterministica**: Grazie ai `DeadlineEvent`, lo scheduler *non può saltare* oltre una deadline senza fermarsi a controllare.
2.  **Verifica Robusta in `Job.isDeadlineMissed`**:
    -   Se il job è incompleto all'istante del controllo: `currentTime >= absoluteDeadline`.
    -   Se il job è completo: `completionTime > absoluteDeadline`.

---

## 5. Design Choice: Gestione del Tempo di Completamento

Perché lo **Scheduler** imposta il `completionTime` e non il Job?
- **Disaccoppiamento**: Il Job non deve conoscere l'oggetto `MyClock`.
- **Coerenza**: Solo lo Scheduler sa quando l'esecuzione è avvenuta nel tempo globale.
- **Testabilità**: `Job.execute()` rimane una funzione deterministica che opera solo su durate relative.
