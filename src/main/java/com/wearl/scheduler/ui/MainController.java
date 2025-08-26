package com.example.scheduler.ui;

import com.example.scheduler.core.Algorithm;
import com.example.scheduler.core.algorithms.*;
import com.example.scheduler.core.models.*;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    private final ComboBox<String> algoBox = new ComboBox<>();
    private final TextField quantumField = new TextField("4");
    private final TableView<ProcessSpec> processTable = new TableView<>();
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final javafx.scene.chart.LineChart<Number,Number> gantt =
            new javafx.scene.chart.LineChart<>(xAxis, yAxis);
    private final ToggleButton aiAssist = new ToggleButton("AI Assist");

    public Scene createScene() {
        algoBox.getItems().addAll("FCFS","SJF","SRTF","Priority (NP)","Priority (P)","Round Robin");
        algoBox.getSelectionModel().selectFirst();

        var columns = List.of(
            new TableColumn<ProcessSpec, String>("ID"),
            new TableColumn<ProcessSpec, Number>("Arrival"),
            new TableColumn<ProcessSpec, Number>("Burst"),
            new TableColumn<ProcessSpec, Number>("Priority")
        );
        // Minimal table setup with text fields
        var idField = new TextField();
        var arrivalField = new TextField();
        var burstField = new TextField();
        var priorityField = new TextField();

        var addBtn = new Button("Add");
        addBtn.getStyleClass().add("primary");
        addBtn.setOnAction(e -> {
            try {
                processTable.getItems().add(new ProcessSpec(
                    idField.getText().isBlank() ? "P" + (processTable.getItems().size()+1) : idField.getText(),
                    Integer.parseInt(arrivalField.getText()),
                    Integer.parseInt(burstField.getText()),
                    Integer.parseInt(priorityField.getText().isBlank() ? "0" : priorityField.getText())
                ));
                idField.clear(); arrivalField.clear(); burstField.clear(); priorityField.clear();
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });

        var runBtn = new Button("Run");
        runBtn.getStyleClass().add("accent");
        runBtn.setOnAction(e -> runSimulation());

        var clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> { processTable.getItems().clear(); gantt.getData().clear(); });

        var topBar = new HBox(10, new Label("Algorithm:"), algoBox,
                new Label("Quantum:"), quantumField, aiAssist, runBtn, clearBtn);
        topBar.setPadding(new Insets(10));
        topBar.getStyleClass().add("toolbar");

        var form = new HBox(10,
            new VBox(new Label("ID"), idField),
            new VBox(new Label("Arrival"), arrivalField),
            new VBox(new Label("Burst"), burstField),
            new VBox(new Label("Priority"), priorityField),
            addBtn
        );
        form.setPadding(new Insets(10));

        var tableBox = new VBox(new Label("Processes"), processTable);
        VBox.setVgrow(processTable, Priority.ALWAYS);

        xAxis.setLabel("Time");
        yAxis.setLabel("Process index");
        gantt.setCreateSymbols(false);
        gantt.setLegendVisible(false);
        gantt.getStyleClass().add("gantt");

        var center = new VBox(form, gantt);
        VBox.setVgrow(gantt, Priority.ALWAYS);

        var root = new BorderPane(center, topBar, null, tableBox, null);
        Scene scene = new Scene(root, 1100, 720);
        scene.setFill(Color.web("#0f1020"));
        scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
        return scene;
    }

    private void runSimulation() {
        var list = new ArrayList<ProcessSpec>(processTable.getItems());
        if (list.isEmpty()) {
            showError("Add at least one process.");
            return;
        }
        String algo = algoBox.getSelectionModel().getSelectedItem();
        int quantum = parseIntOrDefault(quantumField.getText(), 4);
        if (aiAssist.isSelected()) {
            algo = suggestAlgo(list, quantum);
            algoBox.getSelectionModel().select(algo);
        }
        Algorithm impl = switch (algo) {
            case "FCFS" -> new Fcfs();
            case "SJF" -> new Sjf();
            case "SRTF" -> new Srtf();
            case "Priority (NP)" -> new PriorityNonPreemptive();
            case "Priority (P)" -> new PriorityPreemptive();
            case "Round Robin" -> new RoundRobin();
            default -> new Fcfs();
        };
        var result = impl.schedule(list, quantum);
        renderGantt(list, result.timeline());
        showMetrics(result.metrics());
    }

    private String suggestAlgo(List<ProcessSpec> list, int quantum) {
        // Simple heuristics: high variance in burst -> SRTF; many processes + similar bursts -> RR; otherwise SJF
        double avg = list.stream().mapToInt(ProcessSpec::burst).average().orElse(0);
        double var = list.stream().mapToDouble(p -> Math.pow(p.burst()-avg, 2)).average().orElse(0);
        if (list.size() >= 6 && var < (avg*avg*0.2)) return "Round Robin";
        if (var > (avg*avg*0.5)) return "SRTF";
        return "SJF";
    }

    private void renderGantt(List<ProcessSpec> procs, List<ScheduledSlice> slices) {
        gantt.getData().clear();
        Map<String,Integer> idx = new HashMap<>();
        int i = 1;
        for (var p : procs) idx.put(p.id(), i++);
        var series = new XYChart.Series<Number,Number>();
        int lastEnd = 0;
        for (var s : slices) {
            int y = idx.getOrDefault(s.id(), 0);
            series.getData().add(new XYChart.Data<>(s.start(), y));
            series.getData().add(new XYChart.Data<>(s.end(), y));
            lastEnd = Math.max(lastEnd, s.end());
        }
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(10, lastEnd + 1));
        xAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(Math.max(5, procs.size() + 1));
        yAxis.setTickUnit(1);
        gantt.getData().add(series);
    }

    private void showMetrics(Metrics m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Metrics");
        alert.setHeaderText("Scheduling Metrics");
        alert.setContentText(String.format("""
            Avg Waiting: %.2f
            Avg Turnaround: %.2f
            Avg Response: %.2f
            CPU Utilization: %.2f%%
            Throughput: %.3f
            """, m.avgWaiting(), m.avgTurnaround(), m.avgResponse(), m.utilization(), m.throughput()));
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Input Error");
        a.showAndWait();
    }

    private int parseIntOrDefault(String s, int d) {
        try { return Integer.parseInt(s.trim()); } catch (Exception ignored) { return d; }
    }
}
