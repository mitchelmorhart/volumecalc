package com.example.volumecalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A compatibility layer to replace Room DAO with direct SQLite operations
 */
class LogEntryDao(private val dbHelper: DatabaseHelper) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Cache for LiveData objects
    private val entriesByDateCache = mutableMapOf<String, MutableLiveData<List<LogEntry>>>()
    private val allEntriesLiveData = MutableLiveData<List<LogEntry>>(emptyList())
    private val availableDatesLiveData = MutableLiveData<List<String>>(emptyList())
    private val volumeByDateCache = mutableMapOf<String, MutableLiveData<Float?>>()

    init {
        // Initial load
        refreshData()
    }

    suspend fun insert(logEntry: LogEntry): Long {
        val result = withContext(Dispatchers.IO) {
            dbHelper.insertLogEntry(logEntry)
        }
        refreshData()
        return result
    }

    fun getAllLogEntries(): LiveData<List<LogEntry>> {
        refreshAllEntries()
        return allEntriesLiveData
    }

    fun getLogEntriesByDate(date: String): LiveData<List<LogEntry>> {
        if (!entriesByDateCache.containsKey(date)) {
            entriesByDateCache[date] = MutableLiveData(emptyList())
            refreshEntriesByDate(date)
        }
        return entriesByDateCache[date]!!
    }

    fun getAvailableDates(): LiveData<List<String>> {
        refreshAvailableDates()
        return availableDatesLiveData
    }

    fun getSessionVolumeByDate(date: String): LiveData<Float?> {
        if (!volumeByDateCache.containsKey(date)) {
            volumeByDateCache[date] = MutableLiveData(0f)
            refreshVolumeByDate(date)
        }
        return volumeByDateCache[date]!!
    }

    // Refresh all data in the caches
    private fun refreshData() {
        refreshAllEntries()
        refreshAvailableDates()

        // Refresh date-specific caches
        entriesByDateCache.keys.forEach { date ->
            refreshEntriesByDate(date)
        }
        volumeByDateCache.keys.forEach { date ->
            refreshVolumeByDate(date)
        }
    }

    private fun refreshAllEntries() {
        coroutineScope.launch {
            val entries = withContext(Dispatchers.IO) {
                dbHelper.getAllLogEntries()
            }
            allEntriesLiveData.postValue(entries)
        }
    }

    private fun refreshAvailableDates() {
        coroutineScope.launch {
            val dates = withContext(Dispatchers.IO) {
                dbHelper.getAvailableDates()
            }
            availableDatesLiveData.postValue(dates)
        }
    }

    private fun refreshEntriesByDate(date: String) {
        coroutineScope.launch {
            val entries = withContext(Dispatchers.IO) {
                dbHelper.getLogEntriesByDate(date)
            }
            entriesByDateCache[date]?.postValue(entries)
        }
    }

    private fun refreshVolumeByDate(date: String) {
        coroutineScope.launch {
            val volume = withContext(Dispatchers.IO) {
                dbHelper.getSessionVolumeByDate(date)
            }
            volumeByDateCache[date]?.postValue(volume)
        }
    }
}