# Integrazione Java-Python per l'Analisi dei Dati

Questo documento descrive come il simulatore Java comunica con lo script Python per l'estrazione e l'analisi dei dati di simulazione.

## Architettura della Comunicazione

Il passaggio dei dati avviene tramite **Standard Streams (Pipes)**. Java agisce come produttore di dati, mentre Python agisce come consumatore.

### Motivazione delle Scelte

1.  **Utilizzo di Standard Input (stdin):**
    *   **Perché non i file temporanei?** L'uso di `stdin` evita la latenza di scrittura su disco e non lascia "spazzatura" nel filesystem in caso di interruzione anomala.
    *   **Perché non argomenti CLI?** Gli argomenti della riga di comando hanno limiti di dimensione molto restrittivi imposti dal sistema operativo (spesso pochi KB), inadatti per dataset di simulazione.
2.  **Approccio Streaming (Lato Java):**
    *   Per gestire dataset di grandi dimensioni (migliaia o milioni di campioni), Java non costruisce una singola stringa JSON in memoria.
    *   Utilizza un `BufferedWriter` per scrivere i dati direttamente nello stream di output del processo man mano che itera sui dati raccolti (`TaskExecutionTimeCollector`).
    *   Questo riduce drasticamente l'occupazione di memoria RAM in Java, evitando `OutOfMemoryError`.
3.  **Formato JSON:**
    *   JSON è un formato standard, leggibile e supportato nativamente da Python. Permette una struttura flessibile (mappa di ID Task -> Lista di durate).

## Come Funziona (Dettaglio Tecnico)

### 1. Lato Java: `MyUtils.callPythonExtractor`

*   **Inizializzazione:** Viene creato un `ProcessBuilder` per eseguire `python3 distribution_extractor.py`.
*   **Scrittura Stream:**
    ```java
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
        writer.write("{");
        // Iterazione sui task e campioni...
        writer.write("}");
        writer.flush();
    }
    ```
*   Il `BufferedWriter` svuota il buffer nel sistema operativo, che lo passa al processo Python.
*   Java attende poi la chiusura del processo e legge il suo `stdout` per stampare i risultati dell'analisi sulla console.

### 2. Lato Python: `distribution_extractor.py`

Lo script Python è progettato per essere semplice ed estensibile.

*   **Lettura:** Utilizza `sys.stdin.read()` per leggere l'intero stream inviato da Java.
*   **Parsing:** Converte la stringa JSON in un dizionario Python:
    ```python
    data = json.loads(input_data)
    ```
*   **Analisi:** Per ogni Task ID nel dizionario:
    *   Calcola statistiche descrittive (Media, Min, Max).
    *   Formatta l'output in modo leggibile.
*   **Error Handling:** Lo script gestisce errori di decodifica JSON e comunica eventuali problemi attraverso lo `stderr`, che viene catturato e riportato da Java.

## Possibili Evoluzioni

Per dataset nell'ordine dei Gigabyte, l'attuale `json.loads` di Python caricherebbe comunque l'intero dizionario in RAM. In quel caso, si consiglia:
*   Passare al formato **NDJSON** (New-line Delimited JSON), dove ogni riga è un oggetto JSON indipendente.
*   Utilizzare librerie come `ijson` in Python per un parsing iterativo (streaming) anche lato consumatore.
