package com.wearl.scheduler.core.algorithms;

import com.wearl.scheduler.core.Algorithm;
import com.wearl.scheduler.core.models.*;

import java.util.*;

public class Fcfs implements Algorithm {
    @Override public String name() { return "FCFS"; }

    @Override
    public ScheduleResult schedule(List<ProcessSpec> input, int quantum) {
        var procs = new ArrayList<>(input);
        procs.sort(Comparator.comparingInt(ProcessSpec::arrival));
        int t = 0;
        var slices = new ArrayList<ScheduledSlice>();
        var firstStart = new HashMap<String,Integer>();
        var finish = new HashMap<String,Integer>();
        for (var p : procs) {
            t = Math.max(t, p.arrival());
            firstStart.putIfAbsent(p.id(), t);
            int end = t + p.burst();
            slices.add(new ScheduledSlice(p.id(), t, end));
            finish.put(p.id(), end);
            t = end;
        }
        var m = Metrics.from(procs, firstStart, finish);
        return new ScheduleResult(slices, m);
    }
}
