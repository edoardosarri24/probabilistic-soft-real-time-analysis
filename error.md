# Analisi Tecnica dell'Errore Numerico nei Polinomi di Bernstein

Questo documento analizza il comportamento numerico del simulatore e della libreria Bernstein, con particolare attenzione alla precisione dei risultati ottenuti per polinomi di grado elevato (fino a $d=1000$).

## 1. Il Limite Fisico: IEEE 754 (Double Precision)

Il progetto utilizza il tipo `double` di Java, che segue lo standard **IEEE 754** per la rappresentazione in virgola mobile a 64 bit. Questo standard impone due vincoli fondamentali:

### 1.1 Range (Esponente)
Il valore massimo rappresentabile è circa **$1.79 \times 10^{308}$**.
*   Nel calcolo dei coefficienti di Bernstein, il termine critico è il coefficiente binomiale $\binom{n}{k}$.
*   Per $n=1000$, il valore centrale $\binom{1000}{500}$ è circa **$2.7 \times 10^{299}$**.
*   Questo valore è pericolosamente vicino al limite del `double`, ma è ancora rappresentabile. Per $n \ge 1021$, il calcolo dei binomiali intermedi andrebbe in overflow a `Infinity`.

### 1.2 Precisione (Mantissa)
La mantissa è limitata a 52 bit, il che garantisce circa **15-17 cifre decimali significative**.
*   **Machine Epsilon ($\epsilon$):** La minima differenza tra 1.0 e il numero successivo è circa $2.22 \times 10^{-16}$.
*   Qualsiasi errore nell'ordine di $10^{-16}$ è da considerarsi "rumore di fondo" intrinseco dell'hardware.

---

## 2. Analisi dei Metodi di Conversione

### 2.1 Metodo Diretto (`withDirectCoefficientConversion`)
La formula utilizzata è:
$$b_i = \sum_{j=0}^i \frac{\binom{i}{j}}{\binom{n}{j}} a_j$$

**Stabilità Numerica:**
*   Il metodo è **sorprendentemente stabile** anche per $d=1000$.
*   Sebbene i coefficienti binomiali siano enormi ($10^{299}$), il calcolo del loro rapporto $\frac{\binom{i}{j}}{\binom{n}{j}}$ produce sempre un numero $\le 1$.
*   Poiché i coefficienti del polinomio monomico $a_j$ sono tutti positivi ($1.0$), la somma non presenta "cancellazioni catastrofiche" (sottrazioni tra numeri quasi uguali).
*   **Risultato osservato:** Un errore $L_1 \approx 1.84 \times 10^{-13}$. Questo non è un errore dell'algoritmo, ma l'accumulo fisiologico di 1000 arrotondamenti durante la sommatoria e la valutazione.

### 2.2 Metodo a Matrice (`withMatrixInversion`)
Questo metodo fallisce drasticamente per gradi elevati ($d > 100$).

**Instabilità Numerica:**
*   **Overflow dei prodotti:** La matrice di conversione moltiplica tra loro più coefficienti binomiali. Ad esempio, il calcolo di $\binom{1000}{i} \times \binom{1000-i}{j-i}$ supera rapidamente $10^{308}$, portando a `Infinity`.
*   **Cancellazione Catastrofica:** La formula prevede segni alterni $(-1)^{j-i}$. Quando si sottraggono numeri enormi (nell'ordine di $10^{200}$) che differiscono solo nelle ultime cifre, la precisione viene completamente distrutta, lasciando solo "NaN" o errori enormi.
*   **Risultato osservato:** Per $d=1000$, il box delle distanze riporta `nan` o `inf` a causa della saturazione dei bit.

---

## 3. Origine del Residuo $10^{-13}$ nel Metodo Diretto

L'errore $L_1$ non è "zero matematico" ma $0.000000000000184$. Questo residuo deriva da tre fattori:

1.  **Integrazione di Simpson:** La regola di Simpson campiona il polinomio in un numero finito di punti (es. 100). L'errore di troncamento della formula stessa e l'arrotondamento di ogni valutazione si sommano nel risultato finale dell'integrale.
2.  **Valutazione del Polinomio:** Per valutare $B(x)$, il codice deve calcolare 1001 termini della base di Bernstein. Ognuno di questi calcoli ($x^i$, $(1-x)^{n-i}$, binomiali) introduce un micro-errore di precisione.
3.  **Conversione JSON/Python:** Il passaggio dei dati tramite JSON e la successiva interpretazione in Python (che usa a sua volta i float a 64 bit) mantiene l'errore ma non può eliminarlo.

---

## 4. Conclusioni sulla Robustezza

1.  **Validità del Metodo:** Per i fini del progetto (analisi soft real-time), un errore di $10^{-13}$ è equivalente a **zero**. È invisibile in qualsiasi rappresentazione grafica e non influenza la precisione delle simulazioni.
2.  **Limite del Grado:** Il sistema attuale è robusto fino a un grado di circa **1000-1020**. Oltre questa soglia, sarebbe necessario passare a una rappresentazione logaritmica dei binomiali o all'uso di `BigDecimal`.
3.  **Visualizzazione:** Lo script Python è stato aggiornato per gestire i valori `NaN` e `Infinity`, assicurando che anche in caso di instabilità numerica (tipica del metodo a matrice), il sistema non crashi ma riporti correttamente il fallimento numerico.
