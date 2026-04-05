# Simulator Testing Documentation

This document describes the testing infrastructure for the Discrete Event Simulator (DES), focusing on the new features related to **Deadline Miss Strategies**.

## New Test Scenarios

The `DeadlineMissStrategyTest` class was introduced to verify the behavioral differences between the three available strategies when a job misses its deadline.

### 1. AbortJobStrategy
*   **Goal**: Verify that when a job misses its deadline, it is immediately removed from the scheduler's ready queue.
*   **Verification**:
    *   The simulation continues until the specified duration.
    *   The `AbortedJobsCollector` correctly increments the abort count for the task.
    *   The `TaskExecutionTimeCollector` still contains the sampled execution time (as it is recorded at release time).
    *   No further execution time is consumed by the aborted job.

### 2. AbortSimulationStrategy
*   **Goal**: Verify that the simulation stops immediately upon the first deadline miss.
*   **Verification**:
    *   The scheduler throws a `RuntimeException` at the exact moment of the deadline miss.
    *   The simulation clock matches the absolute deadline of the job that caused the abort.
    *   Ensures strict real-time constraints where any failure is considered fatal.

### 3. ContinueStrategy
*   **Goal**: Verify that the job remains in the system and completes its execution even after the deadline has passed.
*   **Verification**:
    *   The `AbortedJobsCollector` still records the deadline miss.
    *   The job is NOT removed from `readyJobs`.
    *   The simulation reaches the end time.
    *   The job eventually completes, and its execution is fully simulated.
    *   If multiple jobs of the same task are ready (due to the previous one not finishing), the scheduler maintains FIFO order (executing the oldest job first).

## Comparison with Legacy Tests

| Feature | Legacy Tests | New Tests (`DeadlineMissStrategyTest`) |
| :--- | :--- | :--- |
| **Scope** | Fixed behavior (always abort job). | Parameterized behavior via Strategy Pattern. |
| **Error Handling** | Implicitly handled by logging and removing job. | Explicitly tested for simulation termination (`AbortSimulation`). |
| **Soft Real-Time** | Only simulated job abortion. | Supports "Late Completion" scenarios where jobs are never dropped. |
| **Collector Integrity**| Verified counts of aborted vs successful. | Verified that execution times are consistently recorded at release regardless of the outcome. |

## Running Tests
To execute all tests, including the new strategy-specific ones:
```bash
./exec/simulator.sh
```
