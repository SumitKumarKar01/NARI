package com.nari.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LogEntryAdapter(
    var logEntries: List<LogEntry>,
    private val logEntryDao: LogEntryDao,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<LogEntryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteDate: TextView = itemView.findViewById(R.id.noteDate)
        val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        val deleteNoteButton: ImageButton = itemView.findViewById(R.id.deleteNoteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_cards, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val logEntry = logEntries[position]

        // Convert the date from Long to a readable String
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.format(Date(logEntry.date))

        holder.noteDate.text = date
        holder.noteContent.text = logEntry.content

        holder.deleteNoteButton.setOnClickListener {
            // Delete the log entry from the database in a new coroutine
            scope.launch {
                logEntryDao.delete(logEntry)
            }
        }
    }

    override fun getItemCount() = logEntries.size
}