package com.wearl.scheduler.core.algorithms;

import com.wearl.scheduler.core.Algorithm;
import com.wearl.scheduler.core.models.*;

import java.util.*;

public class Srtf implements Algorithm {
    @Override
    public String name() { 
        return "SRTF"; 
    }

    @Override
    public ScheduleResult schedule(List<ProcessSpec> input, int quantum) {
        // Copy input and sort by arrival time
        var procs = new ArrayList<>(input);
        procs.sort(Comparator.comparingInt(ProcessSpec::arrival));

        int t = 0; // current time
        int i = 0; // index into procs
        var slices = new ArrayList<ScheduledSlice>();
        var firstStart = new HashMap<String, Integer>();
        var finish = new HashMap<String, Integer>();

        // Priority queue ordered by remaining time
        record State(ProcessSpec p, int remaining) {}
        var pq = new PriorityQueue<State>(Comparator.comparingInt(State::remaining));

        String runningId = null;
        int runStart = -1;

        while (i < procs.size() || !pq.isEmpty()) {
            // Enqueue processes that have arrived by current time
            while (i < procs.size() && procs.get(i).arrival() <= t) {
                pq.offer(new State(procs.get(i), procs.get(i).burst()));
                i++;
            }

            if (pq.isEmpty()) {
                // No process ready → jump to next arrival
                t = procs.get(i).arrival();
                continue;
            }

            var s = pq.poll();

            // Handle context switch
            if (!Objects.equals(runningId, s.p.id())) {
                if (runningId != null) {
                    slices.add(new ScheduledSlice(runningId, runStart, t));
                }
                runningId = s.p.id();
                runStart = t;
                firstStart.putIfAbsent(runningId, t);
            }

            // Run process until either:
            // - it finishes, or
            // - a new process arrives (causing possible preemption)
            int nextArrival = (i < procs.size()) ? procs.get(i).arrival() : Integer.MAX_VALUE;
            int timeToRun = Math.min(s.remaining, nextArrival - t);

            if (timeToRun <= 0) timeToRun = s.remaining; // last process case

            t += timeToRun;
            int rem = s.remaining - timeToRun;

            if (rem == 0) {
                // Process finished
                finish.put(s.p.id(), t);
            } else {
                // Put back with updated remaining time
                pq.offer(new State(s.p, rem));
            }

            // If preempted or finished → close slice
            if (rem == 0 || (!pq.isEmpty() && pq.peek().remaining < rem)) {
                slices.add(new ScheduledSlice(runningId, runStart, t));
                runningId = null;
            }
        }

        // Close final slice
        if (runningId != null) {
            slices.add(new ScheduledSlice(runningId, runStart, t));
        }

        var m = Metrics.from(input, firstStart, finish);
        return new ScheduleResult(slices, m);
    }
}