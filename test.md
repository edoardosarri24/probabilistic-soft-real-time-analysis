# Report dei Test - Libreria Bernstein

Questo documento descrive i test effettuati sulle classi principali della libreria: `ECDF.java` e `Bernstein.java`.

## 1. ECDF (Empirical Cumulative Distribution Function)
I test per la classe `ECDF` sono contenuti in `ECDFTest.java` e si focalizzano sulla corretta gestione dei dati campionari e sul calcolo della probabilità empirica.

### Casi di Test:
- **Validazione Input:** Verifica che il costruttore rifiuti input `null` o collezioni contenenti valori `null`.
- **Dati Vuoti:** Verifica che `eval(x)` restituisca `0.0` se non sono presenti dati.
- **Caso Singolo Valore:** Verifica il comportamento a gradino con un solo campione.
- **Dati Multipli e Duplicati:** Verifica il calcolo di $P(X \le x)$ con dataset contenenti valori ripetuti, assicurando che la frequenza cumulata sia corretta.
- **Ordinamento Interno:** Conferma che la classe ordini correttamente i dati non ordinati forniti in input per permettere la ricerca binaria.

## 2. Bernstein Polynomial Approximation
I test per la classe `Bernstein` sono contenuti in `BernsteinTest.java` e verificano le diverse basi (Standard, Linear, Exponential) per l'approssimazione di CDF e PDF.

### Casi di Test per la CDF:
- **Validazione Input:** Controllo dei range di supporto (es. $[0, 1]$ per la base normale) e della positività del grado del polinomio.
- **Comportamento ai Limiti:** Verifica che nei punti estremi del supporto ($x=a$ e $x=b$) l'approssimazione rispetti i valori della ECDF (solitamente $0$ e $1$).
- **Partizione dell'Unità:** Verifica che se la funzione target è costantemente $1$, l'approssimazione di Bernstein sia $1$ (proprietà fondamentale delle basi di Bernstein).
- **Monotonia:** Verifica che l'approssimazione di una funzione non decrescente (come la ECDF) resti non decrescente.
- **Base Esponenziale (Semi-infinita):** Verifica della convergenza asintotica a $1.0$ per valori di $x$ molto grandi.

### Casi di Test per la PDF:
- **Non-negatività:** Verifica che l'approssimazione della densità (EPDF) derivata dal polinomio di Bernstein non assuma valori negativi significativi, coerentemente con le proprietà delle distribuzioni di probabilità.
- **Supporto Lineare ed Esponenziale:** Test specifici per le derivate delle basi mappate su intervalli generici e semi-infiniti.

## Esecuzione dei Test
Tutti i test sono stati eseguiti con successo utilizzando lo script di automazione del progetto:
```bash
./exec/bernstein.sh
```
Risultato: `Tests run: 16, Failures: 0, Errors: 0, Skipped: 0`
