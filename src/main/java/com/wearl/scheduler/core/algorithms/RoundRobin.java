package com.wearl.scheduler.core.algorithms;

import com.wearl.scheduler.core.Algorithm;
import com.wearl.scheduler.core.models.*;

import java.util.*;

public class RoundRobin implements Algorithm {
    @Override public String name() { return "Round Robin"; }

    @Override
    public ScheduleResult schedule(List<ProcessSpec> input, int quantum) {
        if (quantum <= 0) quantum = 4;
        var procs = new ArrayList<>(input);
        procs.sort(Comparator.comparingInt(ProcessSpec::arrival));
        int t = 0, i = 0;
        var q = new ArrayDeque<ProcessSpec>();
        var remaining = new HashMap<String,Integer>();
        var slices = new ArrayList<ScheduledSlice>();
        var firstStart = new HashMap<String,Integer>();
        var finish = new HashMap<String,Integer>();

        for (var p : procs) remaining.put(p.id(), p.burst());

        while (i < procs.size() || !q.isEmpty()) {
            while (i < procs.size() && procs.get(i).arrival() <= t) q.offer(procs.get(i++));
            if (q.isEmpty()) { t = Math.max(t, procs.get(i).arrival()); continue; }
            var p = q.poll();
            firstStart.putIfAbsent(p.id(), t);
            int run = Math.min(quantum, remaining.get(p.id()));
            int start = t, end = t + run;
            slices.add(new ScheduledSlice(p.id(), start, end));
            t = end;
            int rem = remaining.get(p.id()) - run;
            remaining.put(p.id(), rem);
            while (i < procs.size() && procs.get(i).arrival() <= t) q.offer(procs.get(i++));
            if (rem > 0) q.offer(p); else finish.put(p.id(), t);
        }
        var m = Metrics.from(procs, firstStart, finish);
        return new ScheduleResult(slices, m);
    }
}
