package com.nari.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "date_ranges")
data class DateRange(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val startDate: Long,
    val endDate: Long
)