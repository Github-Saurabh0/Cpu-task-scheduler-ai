# CPU Task Scheduler (JavaFX)

A desktop CPU scheduling simulator with **Java** and **JavaFX**, featuring FCFS, SJF, SRTF, Priority (P/NP), and Round Robin, a modern UI, a Gantt-like timeline, and metrics (waiting, turnaround, response, utilization, throughput). [Why JavaFX for modern desktop UIs in 2025][4]. [JavaFX + Maven setup guidance][5].

## Run
- Prereqs: JDK 21+ and Maven.
- Dev run:
  - mvn clean javafx:run
- Package:
  - mvn -DskipTests package

## Features
- Algorithms: FCFS, SJF, SRTF, Priority (preemptive/non-preemptive), Round Robin.
- Modern JavaFX UI with CSS styling; AI Assist suggests an algorithm heuristic based on burst variance.
- Metrics dialog and a simple Gantt-style line chart.

## Usage
1. Enter processes (ID, Arrival, Burst, Priority) and add.
2. Pick algorithm; set quantum for Round Robin.
3. Optionally enable AI Assist to auto-suggest an algorithm from workload features.
4. Run to visualize the timeline and view metrics.

## Notes
- JavaFX CSS and Scene Graph enable contemporary desktop interfaces and theming.
- Consider packaging with jlink/jpackage for a standalone app.

[4]: JavaFX relevance in 2025 – TheServerSide article.  
[5]: JavaFX + Spring Boot desktop app guide (style and setup ideas).

