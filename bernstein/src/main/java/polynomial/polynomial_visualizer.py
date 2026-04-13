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
    Produces a plot of the Polynomial, then saves it to a PDF.
    """
    # Takes data.
    x = data.get("x", [])
    y_poly = data.get("y_poly", [])

    if not x or not y_poly:
        print("Error: No data to plot.")
        sys.exit(1)

    # Figure
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(12, 7))
    sns.lineplot(x=x, y=y_poly, label="Polynomial", color="red", linewidth=2)
    plt.title(data.get("title", "Polynomial Visualization"), fontsize=16, pad=20)
    plt.xlabel("x", fontsize=12)
    plt.ylabel("f(x)", fontsize=12)
    plt.legend()

    # Save the plot.
    title = data.get("title", "polynomial")
    # Sanitize title for filename
    safe_title = "".join([c if c.isalnum() else "_" for c in title])
    output_dir = Path("results")
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / f"polynomial_visualization_{safe_title}.pdf"
    plt.savefig(output_path)
    plt.close()

if __name__ == "__main__":
    data = read_data()
    plot_polynomial(data)
    sys.exit(0)
