package com.example.scheduler.core.models;

import java.util.List;
import java.util.Map;

public record Metrics(double avgWaiting, double avgTurnaround, double avgResponse,
                      double utilization, double throughput) {
    public static Metrics from(List<ProcessSpec> procs,
                               Map<String,Integer> firstStart,
                               Map<String,Integer> finish) {
        int n = procs.size();
        double sumWait = 0, sumTat = 0, sumResp = 0;
        int minArrival = procs.stream().mapToInt(ProcessSpec::arrival).min().orElse(0);
        int maxFinish = finish.values().stream().mapToInt(i -> i).max().orElse(0);
        int totalBurst = procs.stream().mapToInt(ProcessSpec::burst).sum();
        for (var p : procs) {
            int tat = finish.get(p.id()) - p.arrival();
            int wait = tat - p.burst();
            int resp = firstStart.get(p.id()) - p.arrival();
            sumTat += tat;
            sumWait += wait;
            sumResp += resp;
        }
        double util = maxFinish > minArrival ? (100.0 * totalBurst) / (maxFinish - minArrival) : 100.0;
        double thr = maxFinish > minArrival ? (1.0 * n) / (maxFinish - minArrival) : n;
        return new Metrics(sumWait / n, sumTat / n, sumResp / n, util, thr);
    }
}
