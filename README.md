# CPU Task Scheduler (JavaFX)

A desktop CPU scheduling simulator built with **Java 21+** and **JavaFX**, featuring:

- Scheduling algorithms: **FCFS**, **SJF**, **SRTF**, **Priority (Preemptive / Non-Preemptive)**, and **Round Robin**.  
- A modern **JavaFX UI** with CSS styling.  
- **Gantt-like timeline visualization**.  
- **Metrics**: waiting time, turnaround time, response time, CPU utilization, throughput.  
- An optional **AI Assist** that heuristically suggests an algorithm based on burst variance.

---

## Model
![Model Main Screen](src/main/resources/CPUTS%20main%20screen.png)

### Prerequisites
- **JDK 21+** installed (`java -version` to verify).
- **Maven 3.9+** installed (`mvn -v` to verify).

### Development Run
```bash
mvn clean javafx:run
```

### Run with AI Assist
```bash
mvn exec:java -Dexec.mainClass="com.wearl.scheduler.ui.App" -Dexec.args="--ai-assist"

```
### Run without AI Assist
```bash
mvn exec:java -Dexec.mainClass="com.wearl.scheduler.ui.App"

```

### Packaging
Build a runnable JAR:
```bash
mvn -DskipTests package
```
The artifact will be at:
```
target/cpu-task-scheduler-1.0.0.jar
```

Run it with:
```bash
java -jar target/cpu-task-scheduler-1.0.0.jar
```

---

##  Features
- **Algorithms**
  - First Come First Serve (FCFS)  
  - Shortest Job First (SJF)  
  - Shortest Remaining Time First (SRTF)  
  - Priority Scheduling (Preemptive and Non-Preemptive)  
  - Round Robin (with configurable quantum)  

- **User Interface**
  - Built with JavaFX + CSS for a clean, modern look.  
  - Timeline visualization for executed slices (Gantt-like chart).  
  - Metrics dialog with averages and per-process statistics.  
  - AI Assist: suggests an algorithm given workload characteristics.  

---

## Usage
1. Enter processes: **ID**, **Arrival**, **Burst**, and (optionally) **Priority**.  
2. Choose a scheduling algorithm.  
   - For Round Robin, set the quantum value.  
3. (Optional) Enable **AI Assist** to let the tool suggest an algorithm.  
4. Click **Run** to visualize the execution timeline and view calculated metrics.  

---

## Notes
- JavaFX provides a contemporary scene graph and CSS theming, making it suitable for modern desktop apps in 2025.  
- For distribution, consider:
  - [`jlink`](https://docs.oracle.com/en/java/javase/21/jlink/) – custom runtime image.  
  - [`jpackage`](https://docs.oracle.com/en/java/javase/21/jpackage/) – native installer packaging.  

---

## References
- [Why JavaFX for modern desktop UIs in 2025][4]  
- [JavaFX + Maven setup guide][5]  

[4]: https://www.theserverside.com/opinion/Does-JavaFX-have-a-future  
[5]: https://openjfx.io/openjfx-docs/#maven


---

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##  Author

**Saurabh**
-  Portfolio: [saurabhh.in](https://saurabhh.in)
-  Email: Saurabh@wearl.co.in

##  Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

##  Issues

If you encounter any issues or have feature requests, please open an issue on the [GitHub repository](https://github.com/Github-Saurabh0/Cpu-task-scheduler-ai.git).

---

⭐ **Star this repository if you find it helpful!** ⭐