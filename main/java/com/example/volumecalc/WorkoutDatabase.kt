package com.example.volumecalc

import android.content.Context

/**
 * A SQLite database implementation that doesn't require Room
 * Acts as a facade over DatabaseHelper to maintain the same API interface
 */
class WorkoutDatabase private constructor(context: Context) {
    // The actual database helper that handles SQLite operations
    private val dbHelper = DatabaseHelper(context)

    // Cached DAO instance
    private val logEntryDaoInstance = LogEntryDao(dbHelper)

    // Provide access to the DAO with the same method name as before
    fun logEntryDao(): LogEntryDao {
        return logEntryDaoInstance
    }

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            // Simple singleton pattern
            return INSTANCE ?: synchronized(this) {
                val instance = WorkoutDatabase(context)
                INSTANCE = instance
                instance
            }
        }
    }
}