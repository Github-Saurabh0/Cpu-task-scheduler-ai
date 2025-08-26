package com.example.scheduler.core.models;

public record ProcessSpec(String id, int arrival, int burst, int priority) {}
