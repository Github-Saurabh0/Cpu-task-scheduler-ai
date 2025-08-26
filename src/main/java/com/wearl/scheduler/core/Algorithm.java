package com.example.scheduler.core;

import com.example.scheduler.core.models.*;
import java.util.List;

public interface Algorithm {
    String name();
    ScheduleResult schedule(List<ProcessSpec> processes, int quantum);
}
