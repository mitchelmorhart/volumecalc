package com.example.volumecalc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "workout_database.db"
        
        // Table name
        private const val TABLE_LOG_ENTRIES = "log_entries"
        
        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_GRADE = "grade"
        private const val COLUMN_ANGLE = "angle"
        private const val COLUMN_ATTEMPTS = "attempts"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_VOLUME = "volume"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_LOG_ENTRIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_GRADE INTEGER NOT NULL,
                $COLUMN_ANGLE INTEGER NOT NULL,
                $COLUMN_ATTEMPTS INTEGER NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_VOLUME REAL NOT NULL
            )
        """.trimIndent()
        
        db.execSQL(createTableQuery)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOG_ENTRIES")
        onCreate(db)
    }
    
    // Insert log entry
    fun insertLogEntry(logEntry: LogEntry): Long {
        val values = ContentValues().apply {
            // Don't include ID as it's auto-generated
            put(COLUMN_TIMESTAMP, logEntry.timestamp)
            put(COLUMN_GRADE, logEntry.grade)
            put(COLUMN_ANGLE, logEntry.angle)
            put(COLUMN_ATTEMPTS, logEntry.attempts)
            put(COLUMN_DATE, logEntry.date)
            put(COLUMN_VOLUME, logEntry.volume)
        }
        
        return writableDatabase.insert(TABLE_LOG_ENTRIES, null, values)
    }
    
    // Get all log entries
    fun getAllLogEntries(): List<LogEntry> {
        val entries = mutableListOf<LogEntry>()
        val query = "SELECT * FROM $TABLE_LOG_ENTRIES ORDER BY $COLUMN_TIMESTAMP DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val grade = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRADE))
                val angle = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANGLE))
                val attempts = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ATTEMPTS))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val volume = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_VOLUME))
                
                entries.add(LogEntry(id, timestamp, grade, angle, attempts, date, volume))
            } while (cursor.moveToNext())
        }
        cursor.close()
        
        return entries
    }
    
    // Get entries by date
    fun getLogEntriesByDate(date: String): List<LogEntry> {
        val entries = mutableListOf<LogEntry>()
        val query = "SELECT * FROM $TABLE_LOG_ENTRIES WHERE $COLUMN_DATE = ? ORDER BY $COLUMN_TIMESTAMP DESC"
        
        val cursor = readableDatabase.rawQuery(query, arrayOf(date))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val grade = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRADE))
                val angle = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANGLE))
                val attempts = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ATTEMPTS))
                val volume = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_VOLUME))
                
                entries.add(LogEntry(id, timestamp, grade, angle, attempts, date, volume))
            } while (cursor.moveToNext())
        }
        cursor.close()
        
        return entries
    }
    
    // Get available dates
    fun getAvailableDates(): List<String> {
        val dates = mutableListOf<String>()
        val query = "SELECT DISTINCT $COLUMN_DATE FROM $TABLE_LOG_ENTRIES ORDER BY $COLUMN_DATE DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                dates.add(date)
            } while (cursor.moveToNext())
        }
        cursor.close()
        
        return dates
    }
    
    // Get session volume by date
    fun getSessionVolumeByDate(date: String): Float {
        var volume = 0f
        val query = "SELECT SUM($COLUMN_VOLUME) as total FROM $TABLE_LOG_ENTRIES WHERE $COLUMN_DATE = ?"
        
        val cursor = readableDatabase.rawQuery(query, arrayOf(date))
        if (cursor.moveToFirst()) {
            volume = cursor.getFloat(0) // First column is the sum
        }
        cursor.close()
        
        return volume
    }
    
    // Delete a log entry
    fun deleteLogEntry(id: Long): Int {
        return writableDatabase.delete(TABLE_LOG_ENTRIES, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}