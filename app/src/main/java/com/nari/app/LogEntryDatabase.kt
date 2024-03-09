package com.nari.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LogEntry::class], version = 1)
abstract class LogEntryDatabase : RoomDatabase() {
    abstract fun logEntryDao(): LogEntryDao
}