package com.example.volumecalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for workout data - simplified version without Room
 */
class WorkoutRepository {
    private val logEntryDao = LogEntryDao()
    val allLogEntries: LiveData<List<LogEntry>> = logEntryDao.allEntries

    // Keep track of available dates with logs
    private val _availableDates = MutableLiveData<List<String>>()
    val availableDates: LiveData<List<String>> = _availableDates

    // Currently selected date for viewing
    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> = _currentDate

    init {
        // Set current date to today
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _currentDate.value = sdf.format(Date())
        updateAvailableDates()
    }

    fun insert(logEntry: LogEntry) {
        logEntryDao.insert(logEntry)
        updateAvailableDates()
    }

    private fun updateAvailableDates() {
        val dates = logEntryDao.getAll()
            .map { it.date }
            .distinct()
            .sortedDescending()
        _availableDates.postValue(dates)
    }

    // Get entries for a specific date
    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        return logEntryDao.getLogEntriesByDate(date)
    }

    // Get the current day's entries
    val currentDayEntries: LiveData<List<LogEntry>> = _currentDate.switchMap { date ->
        getLogEntriesByDate(date)
    }

    // Calculate session volume for a specific date
    fun calculateSessionVolume(date: String): Float {
        return logEntryDao.getAll()
            .filter { it.date == date }
            .sumOf { it.volume.toDouble() }
            .toFloat()
    }

    // Calculate session volume for the current date
    val currentSessionVolume: LiveData<Float> = currentDayEntries.map { entries ->
        entries.sumOf { it.volume.toDouble() }.toFloat()
    }

    // Navigate to the previous day with logs
    fun navigateToPreviousDay(): Boolean {
        val currentDateVal = _currentDate.value ?: return false
        val availableDatesList = _availableDates.value ?: return false

        val currentIndex = availableDatesList.indexOf(currentDateVal)
        if (currentIndex < availableDatesList.size - 1) {
            _currentDate.value = availableDatesList[currentIndex + 1]
            return true
        }
        return false
    }

    // Navigate to the next day with logs
    fun navigateToNextDay(): Boolean {
        val currentDateVal = _currentDate.value ?: return false
        val availableDatesList = _availableDates.value ?: return false

        val currentIndex = availableDatesList.indexOf(currentDateVal)
        if (currentIndex > 0) {
            _currentDate.value = availableDatesList[currentIndex - 1]
            return true
        }
        return false
    }

    // Navigate to today
    fun navigateToToday() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _currentDate.value = sdf.format(Date())
    }

    // Navigate to a specific date
    fun navigateToDate(date: String) {
        _currentDate.value = date
    }
}