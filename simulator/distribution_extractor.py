import sys
import json

def main():
    # Legge i dati da stdin
    input_data = sys.stdin.read()
    
    if not input_data:
        print("Nessun dato ricevuto dallo script Python.")
        return

    try:
        # Carica il JSON
        data = json.loads(input_data)
        
        print("\n=== Risultati Simulazione (Python Extractor) ===")
        for task_id, durations in data.items():
            if not durations:
                print(f"Task {task_id}: Nessuna esecuzione registrata.")
                continue
                
            avg = sum(durations) / len(durations)
            min_val = min(durations)
            max_val = max(durations)
            
            print(f"\nTask {task_id}:")
            print(f"  - Esecuzioni: {len(durations)}")
            print(f"  - Media:      {avg:.3f} ms")
            print(f"  - Min:        {min_val:.3f} ms")
            print(f"  - Max:        {max_val:.3f} ms")
            # Stampa i primi 5 valori se disponibili
            sample = durations[:5]
            print(f"  - Primi 5 campioni: {sample}")
            
    except json.JSONDecodeError as e:
        print(f"Errore nella decodifica dei dati JSON: {e}")
        print("Dati ricevuti:")
        print(input_data)

if __name__ == "__main__":
    main()
