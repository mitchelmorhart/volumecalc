package com.example.volumecalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A simple in-memory implementation of LogEntryDao that doesn't require Room
 */
class LogEntryDao {
    // Thread-safe list to store entries
    private val entries = CopyOnWriteArrayList<LogEntry>()

    // LiveData to observe all entries
    private val _allEntries = MutableLiveData<List<LogEntry>>(emptyList())
    val allEntries: LiveData<List<LogEntry>> = _allEntries

    // Map to store date-specific entries
    private val entriesByDate = mutableMapOf<String, MutableLiveData<List<LogEntry>>>()

    fun insert(logEntry: LogEntry) {
        entries.add(logEntry)
        updateLiveData()
    }

    fun getAll(): List<LogEntry> {
        return entries.sortedByDescending { it.timestamp }
    }

    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        if (!entriesByDate.containsKey(date)) {
            entriesByDate[date] = MutableLiveData(
                entries.filter { it.date == date }
            )
        }
        return entriesByDate[date]!!
    }

    fun deleteAll() {
        entries.clear()
        updateLiveData()
    }

    private fun updateLiveData() {
        // Update the main LiveData
        _allEntries.postValue(getAll())

        // Update each date-specific LiveData
        entriesByDate.forEach { (date, liveData) ->
            liveData.postValue(entries.filter { it.date == date })
        }
    }
}