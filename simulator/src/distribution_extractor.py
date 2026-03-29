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
        print(f"Error in Json decoding: {e}")
        sys.exit(1)

def plot_distributions(data):
    """
    Produces PDF and CDF plots for each task's execution time distribution.
    """
    def plot_task_distribution(task_id, execution_times):
        if not execution_times:
            return
        # Figure setup.
        fig, (pdf, cdf) = plt.subplots(1, 2, figsize=(14, 6))
        fig.suptitle(f"Task {task_id} - Execution Time Distribution", fontsize=16)
        # PDF
        sns.histplot(execution_times, ax=pdf, stat="density", color="skyblue")
        pdf.set(title="Probability Density Function (PDF)", xlabel="Execution Time (ms)", ylabel="Density")
        # CDF
        sns.ecdfplot(execution_times, ax=cdf, color="orange", linewidth=2)
        cdf.set(title="Cumulative Distribution Function (CDF)", xlabel="Execution Time (ms)", ylabel="Cumulative Probability")
        # Layout
        plt.tight_layout(rect=[0, 0.03, 1, 0.97])
        plt.savefig(output_dir / f"task_{task_id}.pdf")
        plt.close(fig)

    # Declarative execution of plotting.
    sns.set_theme(style="whitegrid")
    output_dir = Path("results/distribution")
    output_dir.mkdir(parents=True, exist_ok=True)
    list(map(lambda item: plot_task_distribution(*item), data.items()))

if __name__ == "__main__":
    data = read_data()
    plot_distributions(data)
    sys.exit(0)