package com.nari.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DateRangeViewModel(private val dateRangeDao: DateRangeDao) : ViewModel() {
    val allDateRanges: LiveData<List<DateRange>> = dateRangeDao.getAllDateRanges()

    fun insert(dateRange: DateRange) = viewModelScope.launch {
        dateRangeDao.insert(dateRange)
    }

    fun delete(dateRange: DateRange) = viewModelScope.launch {
        dateRangeDao.delete(dateRange)
    }
}