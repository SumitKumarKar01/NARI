package com.nari.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DateRangeViewModelFactory(private val dateRangeDao: DateRangeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DateRangeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DateRangeViewModel(dateRangeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}