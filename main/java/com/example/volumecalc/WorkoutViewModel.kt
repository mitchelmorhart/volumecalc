package com.example.volumecalc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WorkoutRepository(application)

    val allLogEntries: LiveData<List<LogEntry>> = repository.allLogEntries
    val availableDates: LiveData<List<String>> = repository.availableDates
    val currentDate: LiveData<String> = repository.currentDate
    val currentDayEntries: LiveData<List<LogEntry>> = repository.currentDayEntries
    val currentSessionVolume: LiveData<Float> = repository.currentSessionVolume

    // Update this function to use viewModelScope to call suspend function
    fun insert(logEntry: LogEntry) = viewModelScope.launch {
        repository.insert(logEntry)
    }

    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        return repository.getLogEntriesByDate(date)
    }

    fun calculateSessionVolume(date: String): Float {
        return repository.calculateSessionVolume(date)
    }

    // Calculate volume based on grade, angle, and attempts
    fun calculateVolume(grade: Int, angle: Float, attempts: Int): Float {
        return attempts * (grade * angle)
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