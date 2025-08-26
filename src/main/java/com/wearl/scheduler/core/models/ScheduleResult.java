package com.example.scheduler.core.models;

import java.util.List;

public record ScheduleResult(List<ScheduledSlice> timeline, Metrics metrics) {}
