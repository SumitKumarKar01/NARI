package com.nari.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreviousMenstruation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_menstruation)
        Log.d("ViewDebug", "WORKING")

        val datePicker1: TextView = findViewById(R.id.datePicker1)
        val datePicker2: TextView = findViewById(R.id.datePicker2)

        datePicker1.setOnClickListener {
            showDatePicker(it as TextView)
        }

        datePicker2.setOnClickListener {
            showDatePicker(it as TextView)
        }
    }

    private fun showDatePicker(dateTextView: TextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        datePicker.addOnPositiveButtonClickListener { dateLong ->
            val date = Date(dateLong)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateTextView.text = format.format(date)
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
}