package com.example.volumecalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.volumecalc.databinding.FragmentTrackBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import kotlin.math.roundToInt

class TrackFragment : Fragment() {
    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    // Using float for angle to handle 2.5 degree increments
    private var grade = 0
    private var angle = 0.0f
    private var attempts = 0
    private var ignoreTextChanges = false
    private lateinit var workoutViewModel: WorkoutViewModel

    // Constants for max values
    companion object {
        const val MAX_GRADE = 17
        const val MAX_ANGLE = 70.0f
        const val ANGLE_INCREMENT = 2.5f
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrackBinding.inflate(inflater, container, false)

        // Initialize the ViewModel
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]

        setupTextWatchers()

        binding.btnGradeMinus.setOnClickListener { updateValue(-1, "grade") }
        binding.btnGradePlus.setOnClickListener { updateValue(1, "grade") }
        binding.btnAngleMinus.setOnClickListener { updateValue(-1, "angle") }
        binding.btnAnglePlus.setOnClickListener { updateValue(1, "angle") }
        binding.btnAttemptsMinus.setOnClickListener { updateValue(-1, "attempts") }
        binding.btnAttemptsPlus.setOnClickListener { updateValue(1, "attempts") }

        binding.btnSave.setOnClickListener {
            saveLogEntry()
        }

        binding.btnClear.setOnClickListener {
            grade = 0; angle = 0.0f; attempts = 0
            updateUI()
        }

        updateUI()
        return binding.root
    }

    private fun setupTextWatchers() {
        binding.etGrade.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!ignoreTextChanges) {
                    ignoreTextChanges = true
                    grade = (s.toString().toIntOrNull() ?: 0).coerceIn(0, MAX_GRADE)
                    binding.etGrade.setText(grade.toString())
                    ignoreTextChanges = false
                }
            }
        })

        binding.etAngle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!ignoreTextChanges) {
                    ignoreTextChanges = true
                    try {
                        val inputAngle = s.toString().toFloatOrNull() ?: 0.0f
                        // Round to nearest valid increment (0, 2.5, 5.0, 7.5, etc.)
                        angle = ((inputAngle / ANGLE_INCREMENT).roundToInt() * ANGLE_INCREMENT).coerceIn(0.0f, MAX_ANGLE)
                        binding.etAngle.setText(angle.toString())
                    } catch (e: Exception) {
                        angle = 0.0f
                        binding.etAngle.setText("0.0")
                    }
                    ignoreTextChanges = false
                }
            }
        })

        binding.etAttempts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!ignoreTextChanges) {
                    ignoreTextChanges = true
                    attempts = s.toString().toIntOrNull() ?: 0
                    if (attempts < 0) attempts = 0
                    binding.etAttempts.setText(attempts.toString())
                    ignoreTextChanges = false
                }
            }
        })
    }

    private fun updateValue(delta: Int, field: String) {
        when (field) {
            "grade" -> {
                grade = (grade + delta).coerceIn(0, MAX_GRADE)
            }
            "angle" -> {
                // For angle, increment by 2.5 degrees
                val newAngle = if (delta > 0) {
                    angle + ANGLE_INCREMENT
                } else {
                    angle - ANGLE_INCREMENT
                }
                angle = newAngle.coerceIn(0.0f, MAX_ANGLE)
            }
            "attempts" -> {
                attempts = (attempts + delta).coerceAtLeast(0)
            }
        }
        updateUI()
    }

    // Calculate workout volume based on grade, angle, and attempts
    private fun calculateVolume(grade: Int, angle: Float, attempts: Int): Float {
        // Formula: attempts * (grade * angle)
        return attempts * (grade * angle)
    }

    private fun saveLogEntry() {
        // Get current values from UI
        val gradeValue = binding.etGrade.text.toString().toIntOrNull() ?: 0
        val angleValue = binding.etAngle.text.toString().toFloatOrNull() ?: 0.0f
        val attemptsValue = binding.etAttempts.text.toString().toIntOrNull() ?: 0

        // Calculate volume
        val volume = calculateVolume(gradeValue, angleValue, attemptsValue)

        // Get current date and time
        val currentDate = Date()
        val timestamp = currentDate.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(currentDate)

        // Create log entry with volume
        val logEntry = LogEntry(
            id = System.currentTimeMillis(), // Simple unique ID
            timestamp = timestamp,
            grade = gradeValue,
            angle = angleValue.toInt(), // Store as int for now
            attempts = attemptsValue,
            date = dateString,
            volume = volume // Add the volume
        )

        // Save to repository
        workoutViewModel.insert(logEntry)

        // Show confirmation to user with volume info
        Toast.makeText(context, "Workout saved! Volume: ${"%.1f".format(volume)}", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        ignoreTextChanges = true
        binding.etGrade.setText(grade.toString())
        binding.etAngle.setText(angle.toString())
        binding.etAttempts.setText(attempts.toString())

        // Update the volume preview
        val previewVolume = calculateVolume(grade, angle, attempts)
        binding.tvVolumePreview?.text = "Volume: ${"%.1f".format(previewVolume)}"

        ignoreTextChanges = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}