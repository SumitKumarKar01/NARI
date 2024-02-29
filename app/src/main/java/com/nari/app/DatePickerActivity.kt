package com.nari.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class DatePickerActivity : AppCompatActivity() {
    private lateinit var selectedDateTextView: TextView
    private lateinit var datePicker: DatePicker
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_picker)

        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        datePicker = findViewById(R.id.datePicker)
        confirmButton = findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {
            // Save selected date to SharedPreferences
            saveSelectedDateToSharedPreferences()

            // Hide DatePicker
            datePicker.visibility = View.INVISIBLE
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun saveSelectedDateToSharedPreferences() {
        val selectedDate = getSelectedDateFromDatePicker()
        val periodDate: SharedPreferences = getSharedPreferences("PeriodDate", Context.MODE_PRIVATE)
        val editor = periodDate.edit()
        editor.putString("selectedDate", selectedDate)
        Log.d("DatePickerActivity", "Selected date: $selectedDate")
        editor.apply()
        selectedDateTextView.text = getString(R.string.selectedDate, selectedDate)

    }

    private fun getSelectedDateFromDatePicker(): String {
        val year = datePicker.year
        val month = datePicker.month
        val day = datePicker.dayOfMonth

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

}