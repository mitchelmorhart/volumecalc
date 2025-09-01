package com.example.volumecalc

/**
 * An in-memory database implementation that doesn't require Room
 */
class WorkoutDatabase {
    // Create a singleton DAO
    private val logEntryDao = LogEntryDao()

    // Provide access to the DAO
    fun logEntryDao(): LogEntryDao {
        return logEntryDao
    }

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Any): WorkoutDatabase {
            // Simple singleton pattern
            return INSTANCE ?: synchronized(this) {
                val instance = WorkoutDatabase()
                INSTANCE = instance
                instance
            }
        }
    }
}