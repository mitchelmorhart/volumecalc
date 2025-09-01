package com.example.volumecalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for workout data - simplified version without Room
 */
class WorkoutViewModel : ViewModel() {
    private val repository = WorkoutRepository()
    val allLogEntries: LiveData<List<LogEntry>> = repository.allLogEntries

    // Available dates with workout logs
    val availableDates: LiveData<List<String>> = repository.availableDates

    // Currently selected date
    val currentDate: LiveData<String> = repository.currentDate

    // Entries for the currently selected date
    val currentDayEntries: LiveData<List<LogEntry>> = repository.currentDayEntries

    // Session volume for the currently selected date
    val currentSessionVolume: LiveData<Float> = repository.currentSessionVolume

    fun insert(logEntry: LogEntry) {
        repository.insert(logEntry)
    }

    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        return repository.getLogEntriesByDate(date)
    }

    fun calculateSessionVolume(date: String): Float {
        return repository.calculateSessionVolume(date)
    }

    // Navigation methods
    fun navigateToPreviousDay(): Boolean {
        return repository.navigateToPreviousDay()
    }

    fun navigateToNextDay(): Boolean {
        return repository.navigateToNextDay()
    }

    fun navigateToToday() {
        repository.navigateToToday()
    }

    fun navigateToDate(date: String) {
        repository.navigateToDate(date)
    }
}