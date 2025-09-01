package com.example.volumecalc

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for workout data using SQLite implementation
 */
class WorkoutRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val logEntryDao = LogEntryDao(dbHelper)

    val allLogEntries: LiveData<List<LogEntry>> = logEntryDao.getAllLogEntries()
    val availableDates: LiveData<List<String>> = logEntryDao.getAvailableDates()

    // Currently selected date for viewing
    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> = _currentDate

    init {
        // Set current date to today
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _currentDate.value = sdf.format(Date())
    }

    suspend fun insert(logEntry: LogEntry): Long {
        return logEntryDao.insert(logEntry)
    }

    // Get entries for a specific date
    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        return logEntryDao.getLogEntriesByDate(date)
    }

    // Get the current day's entries
    val currentDayEntries: LiveData<List<LogEntry>> = _currentDate.switchMap { date ->
        getLogEntriesByDate(date)
    }

    // Get volume for the current date
    val currentSessionVolume: LiveData<Float> = _currentDate.switchMap { date ->
        logEntryDao.getSessionVolumeByDate(date).map { it ?: 0f }
    }

    // Calculate session volume for a specific date
    fun calculateSessionVolume(date: String): Float {
        return dbHelper.getSessionVolumeByDate(date)
    }

    // Navigation methods remain the same
    fun navigateToPreviousDay(): Boolean {
        val currentDateVal = _currentDate.value ?: return false
        val availableDatesList = availableDates.value ?: return false

        val currentIndex = availableDatesList.indexOf(currentDateVal)
        if (currentIndex < availableDatesList.size - 1) {
            _currentDate.value = availableDatesList[currentIndex + 1]
            return true
        }
        return false
    }

    fun navigateToNextDay(): Boolean {
        val currentDateVal = _currentDate.value ?: return false
        val availableDatesList = availableDates.value ?: return false

        val currentIndex = availableDatesList.indexOf(currentDateVal)
        if (currentIndex > 0) {
            _currentDate.value = availableDatesList[currentIndex - 1]
            return true
        }
        return false
    }

    fun navigateToToday() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _currentDate.value = sdf.format(Date())
    }

    fun navigateToDate(date: String) {
        _currentDate.value = date
    }
}