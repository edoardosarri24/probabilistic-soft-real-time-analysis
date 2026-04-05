#!/usr/bin/env zsh

set -e

# Load SDKMAN! in the script
export SDKMAN_DIR="$HOME/.sdkman"
if [[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]]; then
    source "$SDKMAN_DIR/bin/sdkman-init.sh"
else
    echo "Errore: SDKMAN! non trovato in $SDKMAN_DIR"
    exit 1
fi

# Execute the script
cd simulator || { echo "Error: 'simulator' folder doesn't exist"; exit 1; }
sdk use java 24.0.2-tem
mvn clean validate compile test exec:java
cd ..