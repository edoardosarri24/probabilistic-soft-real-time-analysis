#!/bin/bash

# Load SDKMAN! in the script
export SDKMAN_DIR="$HOME/.sdkman"
if [[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]]; then
    source "$SDKMAN_DIR/bin/sdkman-init.sh"
else
    echo "Errore: SDKMAN! non trovato in $SDKMAN_DIR"
    exit 1
fi

# Execute the script
cd simulator || { echo "Errore: cartella 'simulator' non trovata"; exit 1; }
sdk use java 24.0.2-tem
mvn clean compile exec:java -U
cd ..