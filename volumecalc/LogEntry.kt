package com.example.volumecalc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_entries")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val grade: Int,
    val angle: Int,
    val attempts: Int,
    val date: String = "",
    val volume: Float = 0.0f // Calculated field for volume
)