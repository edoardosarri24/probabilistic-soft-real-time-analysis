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
import numpy as np
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

def plot_polynomial_distance(data):
    """
    Produces a plot of two Polynomials and their distance, then saves it to a PDF.
    """
    # Takes data.
    x = np.array(data.get("x", []))
    y1 = np.array(data.get("y1", []))
    y2 = np.array(data.get("y2", []))
    norm_l1 = float(data.get("norm_l1", 0.0))
    norm_l2 = float(data.get("norm_l2", 0.0))
    norm_linf = float(data.get("norm_linf", 0.0))
    if len(x) == 0 or len(y1) == 0 or len(y2) == 0:
        print("Error: No data to plot.")
        sys.exit(1)

    # Figure
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(12, 7))
    title = data.get("title", "Polynomial Distance Visualization")
    plt.title(title, fontsize=16, pad=20)
    plt.xlabel("x", fontsize=12)
    plt.ylabel("f(x)", fontsize=12)
    plt.plot(x, y1, label="Polynomial 1", color="blue", linewidth=2)
    plt.plot(x, y2, label="Polynomial 2", color="red", linewidth=2)
    plt.fill_between(x, y1, y2, color='gray', alpha=0.15, label="Distance Area")

    # Legend and metrics
    plt.legend(loc='upper left', frameon=True)
    dist_title = "Distances:"
    dist_metrics = f"L1: {norm_l1:.15f}\nL2: {norm_l2:.15f}\nL∞: {norm_linf:.15f}"
    full_text = f"{dist_title}\n{dist_metrics}"
    props = dict(boxstyle='round,pad=0.5', facecolor='white', alpha=0.9, edgecolor='lightgray')
    plt.gca().text(0.015, 0.82, full_text, transform=plt.gca().transAxes, fontsize=11, verticalalignment='top', bbox=props)

    # Save the plot.
    title_for_file = data.get("title", "distance")
    safe_title = "".join([c if c.isalnum() else "_" for c in title_for_file])
    output_dir = Path("results")
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / f"polynomial_distance_visualization_{safe_title}.pdf"
    plt.savefig(output_path)
    plt.close()

if __name__ == "__main__":
    data = read_data()
    plot_polynomial_distance(data)
    sys.exit(0)
