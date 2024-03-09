package com.nari.app
import androidx.room.Database
import androidx.room.RoomDatabase
import com.nari.app.DateRange
import com.nari.app.DateRangeDao

@Database(entities = [DateRange::class], version = 1)
abstract class DateRangeDatabase : RoomDatabase() {
    abstract fun dateRangeDao(): DateRangeDao
}