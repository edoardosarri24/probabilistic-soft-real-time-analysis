# Roadmap Sviluppo Libreria Polinomi di Bernstein

Questo documento riassume i task immediati per l'implementazione della libreria Java dedicata ai polinomi di Bernstein.

---

## 1. Struttura Core della Libreria (Java)
[cite_start]L'obiettivo è creare una base solida per la rappresentazione e manipolazione dei polinomi[cite: 2, 49].

- [ ] [cite_start]**Classe `BernsteinBasis`**: Implementare la logica per la base di Bernstein[cite: 49, 50].
- [ ] [cite_start]**Classe `BernsteinPolynomial`**: Implementare la classe che rappresenta il polinomio vero e proprio[cite: 49, 50].
- [ ] **Metodi di Costruzione**:
    - [ ] [cite_start]Creare un metodo statico che, fissati i campioni di una funzione su una griglia, costruisca il polinomio di Bernstein corrispondente[cite: 50, 51].
    - [ ] [cite_start]Implementare la logica per costruire il polinomio partendo dai campioni della **CDF** (funzione di ripartizione)[cite: 52, 55].

---

## 2. Operazioni Matematiche e Analitiche
[cite_start]Queste funzionalità serviranno per calcolare la distribuzione del tempo di risposta[cite: 61, 73].

- [ ] [cite_start]**Calcolo della Derivata**: Implementare un metodo per derivare il polinomio di Bernstein[cite: 67, 73]. 
    > [cite_start]*Nota: Derivare il polinomio costruito sulla CDF è un buon modo per approssimare la PDF[cite: 52, 53].*
- [ ] [cite_start]**Gestione dei Campioni**: Implementare la logica per gestire campioni fissati (grid) per garantire che il polinomio passi per i valori estremi[cite: 62, 63, 67].

---

## 3. Ottimizzazioni Computational
[cite_start]Per evitare che la complessità "esploda in mano" durante le convoluzioni[cite: 15, 65].

- [ ] [cite_start]**Mappa Coefficienti Binomiali**: Implementare una mappa (o tabella pre-calcolata) per i coefficienti binomiali[cite: 73, 74].
    - [ ] [cite_start]La mappa deve supportare gradi fino a $2n$ (necessario per la convoluzione di due polinomi di grado $n$)[cite: 65, 74].
- [ ] [cite_start]**Efficienza**: Assicurarsi che i metodi siano prevalentemente statici per facilitare il riutilizzo e il calcolo veloce[cite: 50, 75].

---

## 4. Validazione e Prossimi Passi
Prima di procedere con l'implementazione massiccia della logica di convoluzione avanzata:

1.  [cite_start]**Diagramma delle Classi**: Progettare la struttura delle classi e inviarla via email alla prof per approvazione[cite: 22].
2.  [cite_start]**Integrazione con il Simulatore**: Preparare la libreria per essere confrontata con i dati estratti dal simulatore (già esistente) per ottenere la "ground truth"[cite: 17, 22, 26].
3.  [cite_start]**Sirio**: Verificare la compatibilità per l'integrazione con Sirio per la rappresentazione delle distribuzioni[cite: 3, 31].

---

## Note Teoriche da Approfondire (Step Successivi)
*Non implementare ora, ma tenere in considerazione per il design:*
- [cite_start]Estensione a **Bernstein Exponential**[cite: 50, 76].
- [cite_start]Tecniche di **riduzione del grado** da $2n$ a $n$ dopo la convoluzione[cite: 65, 78].
- [cite_start]Garanzia di **ordinamento stocastico** per i sistemi real-time[cite: 76, 78].