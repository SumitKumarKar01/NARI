package com.nari.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateRangeAdapter(var dateRanges: List<DateRange>, private val dateRangeDao: DateRangeDao) : RecyclerView.Adapter<DateRangeAdapter.DateRangeViewHolder>() {

    class DateRangeViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateRangeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_cards, parent, false)
        return DateRangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateRangeViewHolder, position: Int) {
        val dateRange = dateRanges[position]
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedStartDate = format.format(Date(dateRange.startDate))
        val formattedEndDate = format.format(Date(dateRange.endDate))
        val startDateTextView: TextView = holder.view.findViewById(R.id.periodStartDate)
        val endDateTextView: TextView = holder.view.findViewById(R.id.periodEndDate)
        startDateTextView.text = formattedStartDate
        endDateTextView.text = formattedEndDate

        val deleteButton: ImageButton = holder.view.findViewById(R.id.deleteDateButton)
        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dateRangeDao.delete(dateRange)
                withContext(Dispatchers.Main) {
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount() = dateRanges.size
}