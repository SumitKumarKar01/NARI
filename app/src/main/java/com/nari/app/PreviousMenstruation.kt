package com.nari.app

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
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

        val addDateButton: ImageButton = findViewById(R.id.addDateButton)
        addDateButton.setOnClickListener {
            val builder: MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
            val picker: MaterialDatePicker<Pair<Long, Long>> = builder.build()

            picker.addOnPositiveButtonClickListener { dateRange ->
                if (dateRange != null) {
                    val startDate = Date(dateRange.first)
                    val endDate = Date(dateRange.second)

                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedStartDate = format.format(startDate)
                    val formattedEndDate = format.format(endDate)

                    Log.d("DatePicker", "Start Date: $formattedStartDate, End Date: $formattedEndDate")
                }
            }

            picker.show(supportFragmentManager, picker.toString())
        }
    }


}