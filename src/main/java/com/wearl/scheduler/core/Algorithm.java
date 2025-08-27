package com.wearl.scheduler.core;

import com.wearl.scheduler.core.models.*;
import java.util.List;

public interface Algorithm {
    String name();
    ScheduleResult schedule(List<ProcessSpec> processes, int quantum);
}
