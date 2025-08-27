package com.wearl.scheduler.core.models;

public record ProcessSpec(String id, int arrival, int burst, int priority) {}
