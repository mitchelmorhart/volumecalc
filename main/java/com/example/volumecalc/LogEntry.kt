package com.example.volumecalc

// Simple data class without Room annotations
data class LogEntry(
    val id: Long = 0,
    val timestamp: Long,
    val grade: Int,
    val angle: Int,
    val attempts: Int,
    val date: String = "",
    val volume: Float = 0.0f
)