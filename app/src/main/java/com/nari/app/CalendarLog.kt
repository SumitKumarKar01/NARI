package com.nari.app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarLog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_log)

        val db = Room.databaseBuilder(
            applicationContext,
            LogEntryDatabase::class.java, "log_entry_database"
        ).build()

        val logEntryDao = db.logEntryDao()

        val recyclerView: RecyclerView = findViewById(R.id.calendarLogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = LogEntryAdapter(emptyList(), logEntryDao, lifecycleScope)
        recyclerView.adapter = adapter

        logEntryDao.getAll().observe(this) { logEntries ->
            // Update the cached copy of the log entries in the adapter.
            adapter.logEntries = logEntries
            adapter.notifyDataSetChanged()
        }

        val confirmNoteButton = findViewById<ImageButton>(R.id.confirmNoteButton)
        confirmNoteButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val addNoteButton = findViewById<ImageButton>(R.id.addNoteButton)

        addNoteButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_note)

            val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
            val noteEditText = dialog.findViewById<EditText>(R.id.noteEditText)
            val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)

            confirmButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month
                val day = datePicker.dayOfMonth

                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val note = noteEditText.text.toString()

                val logEntry = LogEntry(date = calendar.timeInMillis, content = note)

                // Insert the new log entry into the database in a new coroutine
                lifecycleScope.launch {
                    logEntryDao.insert(logEntry)
                }

                dialog.dismiss()
            }

            dialog.show()
        }




    }
}