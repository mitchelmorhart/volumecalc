package com.example.volumecalc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var logEntries: List<LogEntry> = emptyList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        val tvVolume: TextView = itemView.findViewById(R.id.tvVolume)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = logEntries[position]

        // Format date
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(Date(entry.timestamp))

        holder.tvDate.text = formattedDate
        holder.tvDetails.text = "Grade: ${entry.grade}, Angle: ${entry.angle}°, Attempts: ${entry.attempts}"
        holder.tvVolume.text = "Volume: ${"%.1f".format(entry.volume)}"
    }

    override fun getItemCount() = logEntries.size

    // Add this method to update the data
    fun updateData(newLogEntries: List<LogEntry>) {
        logEntries = newLogEntries
        notifyDataSetChanged()
    }
}