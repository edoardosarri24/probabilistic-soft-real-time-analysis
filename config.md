# Configurazione del Simulatore

Questo documento descrive il sistema di configurazione dinamica introdotto nel simulatore, che permette di definire scenari di simulazione tramite file **JSON** o **YAML** senza dover ricompilare il codice sorgente.

## Architettura del Sistema

Il sistema si basa su un pattern **DTO (Data Transfer Object)** situato nel package `config`. Il caricamento segue questo flusso:
1. Lettura del file (JSON o YAML).
2. Deserializzazione in oggetti `SimulationConfig` tramite **Jackson**.
3. Conversione (mappatura) dagli oggetti di configurazione agli oggetti del modello di dominio (`TaskSet`, `Task`, `Sampler`, `Strategy`).

### Supporto Polimorfico
Il simulatore utilizza le annotazioni di Jackson (`@JsonTypeInfo` e `@JsonSubTypes`) per gestire il polimorfismo. Questo permette di definire diverse implementazioni per sampler e strategie usando una proprietà `type` nel file di configurazione.

#### Sampler Supportati
- `deterministic`: Richiede un campo `value` (BigDecimal).
- `discreteChoice`: Richiede una lista di `values` e una lista di `probabilities`.

#### Strategie di Deadline Miss Supportate
- `continue`: Il job continua l'esecuzione anche se ha superato la deadline.
- `abortJob`: Il job viene interrotto immediatamente al mancare della deadline.
- `abortSimulation`: L'intera simulazione viene interrotta al primo errore.

---

## Struttura del File

### Root (`SimulationConfig`)
| Campo | Tipo | Descrizione |
| :--- | :--- | :--- |
| `simulationDurationMs` | double | Durata totale della simulazione in millisecondi. |
| `deadlineMissStrategy` | Oggetto | Definizione della strategia (deve avere il campo `type`). |
| `tasks` | Lista | Elenco dei task da simulare. |

### Task (`TaskConfig`)
| Campo | Tipo | Descrizione |
| :--- | :--- | :--- |
| `period` | Oggetto | Sampler per l'inter-arrival time (deve avere il campo `type`). |
| `deadlineMs` | double | Deadline relativa in millisecondi. |
| `executionTime` | Oggetto | Sampler per il tempo di esecuzione (deve avere il campo `type`). |
| `firstReleaseTime` | Oggetto | (Opzionale) Sampler per il tempo di rilascio del primo job. |

---

## Tecnologie Utilizzate

- **Jackson Databind**: Per la mappatura automatica tra file e classi Java.
- **Jackson YAML Dataformat**: Estensione per il supporto nativo al formato YAML.
- **Java Streams**: Utilizzati per la conversione efficiente delle liste di configurazione in oggetti di dominio.

## Come Aggiungere Nuovi Tipi
Per estendere il configuratore (es. aggiungere un nuovo `Sampler`):
1. Creare la classe di configurazione in `config/SamplerConfig.java` come sottoclasse.
2. Aggiungere il nuovo tipo nell'annotazione `@JsonSubTypes` della classe base.
3. Implementare il metodo `toSampler()` che istanzia l'oggetto di dominio reale.
