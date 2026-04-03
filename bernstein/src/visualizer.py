# /// script
# dependencies = [
#   "seaborn",
#   "matplotlib",
#   "pandas",
#   "numpy",
# ]
# ///

import sys
import json
import seaborn as sns
import matplotlib.pyplot as plt
from pathlib import Path

def read_data():
    raw_input = sys.stdin.read().strip()
    if not raw_input:
        print("No data received.")
        sys.exit(1)
    try:
        return json.loads(raw_input)
    except json.JSONDecodeError as e:
        print(f"Error in JSON decoding: {e}")
        sys.exit(1)

def plot_polynomial(data):
    """
    Produces a plot of the Bernstein Polynomial and saves it to a PDF.
    """
    # Takes data.
    x = data.get("x", [])
    y = data.get("y", [])
    if not x or not y:
        print("Error: No data to plot.")
        sys.exit(1)
    # Figure
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(10, 6))
    sns.lineplot(x=x, y=y, label="Bernstein Polynomial", color="red", linewidth=2)
    plt.title("Bernstein Polynomial", fontsize=16)
    plt.xlabel("x", fontsize=12)
    plt.ylabel("f(x)", fontsize=12)
    # Save the plot.
    output_dir = Path("results")
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / "polynomial.pdf"
    plt.savefig(output_path)
    plt.close()

if __name__ == "__main__":
    data = read_data()
    plot_polynomial(data)
    sys.exit(0)
