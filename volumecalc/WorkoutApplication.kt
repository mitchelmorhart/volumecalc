package com.example.volumecalc

import android.app.Application

/**
 * Application class to provide app-level dependencies
 * Simplified version without Room database
 */
class WorkoutApplication : Application() {
    // Create a singleton repository instance
    val repository: WorkoutRepository by lazy {
        WorkoutRepository()
    }
}