package com.nari.app
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DateRange::class], version = 1, exportSchema = false)
abstract class DateRangeDatabase : RoomDatabase() {
    abstract fun dateRangeDao(): DateRangeDao
}