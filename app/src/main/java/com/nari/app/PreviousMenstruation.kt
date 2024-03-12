package com.nari.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreviousMenstruation : AppCompatActivity() {
    private lateinit var dateRangeViewModel: DateRangeViewModel
    private lateinit var dateRangeDao: DateRangeDao



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_menstruation)
        Log.d("ViewDebug", "WORKING")
        val db = Room.databaseBuilder(
            applicationContext,
            DateRangeDatabase::class.java, "date_ranges"
        ).build()

        dateRangeDao = db.dateRangeDao()

        val viewModelFactory = DateRangeViewModelFactory(dateRangeDao)
        dateRangeViewModel = ViewModelProvider(this, viewModelFactory)[DateRangeViewModel::class.java]


        val recyclerView: RecyclerView = findViewById(R.id.previousMenstruationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DateRangeAdapter(emptyList(), dateRangeDao)
        recyclerView.adapter = adapter


        dateRangeViewModel = ViewModelProvider(this)[DateRangeViewModel::class.java]
        dateRangeViewModel.allDateRanges.observe(this, { dateRanges ->
            // Update the cached copy of the date ranges in the adapter.
            adapter.dateRanges = dateRanges
            adapter.notifyDataSetChanged()
        })

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
            dateRangeViewModel.allDateRanges.observe(this@PreviousMenstruation, { allDateRanges ->
                CoroutineScope(Dispatchers.IO).launch {
                    allDateRanges.forEach { dateRange ->
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedStartDate = format.format(Date(dateRange.startDate))
                        val formattedEndDate = format.format(Date(dateRange.endDate))
                        Log.d("DateRange", "Start Date: $formattedStartDate, End Date: $formattedEndDate")
                    }
                }
            })

            startActivity(Intent(this, MainActivity::class.java))
        }

    }



}