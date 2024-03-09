package com.nari.app

import com.nari.app.DateRangeDatabase
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreviousMenstruation : AppCompatActivity() {
    private lateinit var db: DateRangeDatabase
    private lateinit var dateRangeDao: DateRangeDao



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_menstruation)
        db = Room.databaseBuilder(
            applicationContext,
            DateRangeDatabase::class.java, "date-range-database"
        ).build()

        dateRangeDao = db.dateRangeDao()
        Log.d("ViewDebug", "WORKING")

        val addDateButton: ImageButton = findViewById(R.id.addDateButton)
        addDateButton.setOnClickListener {
            val builder: MaterialDatePicker.Builder<Pair<Long, Long>> = MaterialDatePicker.Builder.dateRangePicker()
            val picker: MaterialDatePicker<Pair<Long, Long>> = builder.build()

            picker.addOnPositiveButtonClickListener { dateRange ->
                if (dateRange != null) {
                    val startDate = dateRange.first
                    val endDate = dateRange.second

                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedStartDate = format.format(Date(startDate))
                    val formattedEndDate = format.format(Date(endDate))

                    Log.d("DatePicker", "Start Date: $formattedStartDate, End Date: $formattedEndDate")

                    val dateRangeEntity = DateRange(startDate = startDate, endDate = endDate)
                    CoroutineScope(Dispatchers.IO).launch {
                        dateRangeDao.insert(dateRangeEntity)
                    }
                }
            }

            picker.show(supportFragmentManager, picker.toString())
        }
        val confirmDateButton: ImageButton = findViewById(R.id.confirmDateButton)
        confirmDateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val allDateRanges = dateRangeDao.getAllDateRanges()
                allDateRanges.forEach { dateRange ->
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedStartDate = format.format(Date(dateRange.startDate))
                    val formattedEndDate = format.format(Date(dateRange.endDate))
                    Log.d("DateRange", "Start Date: $formattedStartDate, End Date: $formattedEndDate")
                }
            }
        }

    }


}