package com.example.scheduler.core.algorithms;

import com.example.scheduler.core.Algorithm;
import com.example.scheduler.core.models.*;

import java.util.*;

public class PriorityNonPreemptive implements Algorithm {
    @Override public String name() { return "Priority (NP)"; }

    @Override
    public ScheduleResult schedule(List<ProcessSpec> input, int quantum) {
        var procs = new ArrayList<>(input);
        procs.sort(Comparator.comparingInt(ProcessSpec::arrival));
        int t = 0, i = 0;
        var pq = new PriorityQueue<ProcessSpec>(Comparator.comparingInt(ProcessSpec::priority));
        var slices = new ArrayList<ScheduledSlice>();
        var firstStart = new HashMap<String,Integer>();
        var finish = new HashMap<String,Integer>();
        while (i < procs.size() || !pq.isEmpty()) {
            while (i < procs.size() && procs.get(i).arrival() <= t) pq.offer(procs.get(i++));
            if (pq.isEmpty()) { t = Math.max(t, procs.get(i).arrival()); continue; }
            var p = pq.poll();
            firstStart.putIfAbsent(p.id(), t);
            int end = t + p.burst();
            slices.add(new ScheduledSlice(p.id(), t, end));
            finish.put(p.id(), end);
            t = end;
        }
        var m = Metrics.from(input, firstStart, finish);
        return new ScheduleResult(slices, m);
    }
}
