package com.example.scheduler.core.algorithms;

import com.example.scheduler.core.Algorithm;
import com.example.scheduler.core.models.*;

import java.util.*;

public class PriorityPreemptive implements Algorithm {
    @Override public String name() { return "Priority (P)"; }

    @Override
    public ScheduleResult schedule(List<ProcessSpec> input, int quantum) {
        record State(ProcessSpec p, int remaining) {}
        var procs = new ArrayList<>(input);
        procs.sort(Comparator.comparingInt(ProcessSpec::arrival));
        int t = 0, i = 0;
        var pq = new PriorityQueue<State>((a,b) -> {
            int cmp = Integer.compare(a.p.priority(), b.p.priority());
            if (cmp != 0) return cmp;
            return Integer.compare(a.remaining, b.remaining);
        });
        var slices = new ArrayList<ScheduledSlice>();
        var firstStart = new HashMap<String,Integer>();
        var finish = new HashMap<String,Integer>();
        String runningId = null; int runStart = -1;

        while (i < procs.size() || !pq.isEmpty()) {
            while (i < procs.size() && procs.get(i).arrival() <= t) {
                pq.offer(new State(procs.get(i), procs.get(i).burst()));
                i++;
            }
            if (pq.isEmpty()) { t = Math.max(t, procs.get(i).arrival()); continue; }
            var s = pq.poll();
            if (!Objects.equals(runningId, s.p.id())) {
                if (runningId != null) slices.add(new ScheduledSlice(runningId, runStart, t));
                runningId = s.p.id();
                if (!firstStart.containsKey(s.p.id())) firstStart.put(s.p.id(), t);
                runStart = t;
            }
            int nextArrival = (i < procs.size()) ? procs.get(i).arrival() : Integer.MAX_VALUE;
            int timeToRun = Math.min(s.remaining, nextArrival - t);
            if (timeToRun <= 0) timeToRun = s.remaining;
            t += timeToRun;
            int rem = s.remaining - timeToRun;
            if (rem == 0) {
                finish.put(s.p.id(), t);
                slices.add(new ScheduledSlice(runningId, runStart, t));
                runningId = null;
            } else {
                pq.offer(new State(s.p, rem));
            }
        }
        if (runningId != null) slices.add(new ScheduledSlice(runningId, runStart, t));
        var m = Metrics.from(input, firstStart, finish);
        return new ScheduleResult(slices, m);
    }
}
