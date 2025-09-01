package com.example.volumecalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volumecalc.databinding.FragmentHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.recyclerHistory.layoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter()
        binding.recyclerHistory.adapter = historyAdapter

        // Get the ViewModel
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]

        // Set up navigation buttons
        binding.btnPreviousDay.setOnClickListener {
            workoutViewModel.navigateToPreviousDay()
        }

        binding.btnNextDay.setOnClickListener {
            workoutViewModel.navigateToNextDay()
        }

        binding.btnToday.setOnClickListener {
            workoutViewModel.navigateToToday()
        }

        // Observe currently selected date
        workoutViewModel.currentDate.observe(viewLifecycleOwner) { date ->
            binding.tvCurrentDate.text = formatDate(date)
            updateNavigationButtonStates()
        }

        // Observe log entries for the current date
        workoutViewModel.currentDayEntries.observe(viewLifecycleOwner) { logEntries ->
            historyAdapter.updateData(logEntries)

            // Show empty state if necessary
            if (logEntries.isEmpty()) {
                binding.emptyStateText?.visibility = View.VISIBLE
                binding.tvSessionVolume.visibility = View.GONE
            } else {
                binding.emptyStateText?.visibility = View.GONE
                binding.tvSessionVolume.visibility = View.VISIBLE
            }
        }

        // Observe session volume for current date
        workoutViewModel.currentSessionVolume.observe(viewLifecycleOwner) { volume ->
            binding.tvSessionVolume.text = "Session Volume: ${"%.1f".format(volume)}"
        }

        // Observe available dates for navigation
        workoutViewModel.availableDates.observe(viewLifecycleOwner) {
            updateNavigationButtonStates()
        }
    }

    private fun updateNavigationButtonStates() {
        val currentDateValue = workoutViewModel.currentDate.value ?: return
        val availableDates = workoutViewModel.availableDates.value ?: return

        val currentIndex = availableDates.indexOf(currentDateValue)

        // Enable/disable previous button
        binding.btnPreviousDay.isEnabled = currentIndex < availableDates.size - 1

        // Enable/disable next button
        binding.btnNextDay.isEnabled = currentIndex > 0

        // Today button should only be enabled if we're not on today
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.btnToday.isEnabled = currentDateValue != today
    }

    private fun formatDate(dateString: String): String {
        // Convert from yyyy-MM-dd to a more user-friendly format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

        val date = inputFormat.parse(dateString) ?: return dateString
        return outputFormat.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}