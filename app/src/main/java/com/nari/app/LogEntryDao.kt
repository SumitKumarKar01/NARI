package com.nari.app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogEntryDao {
    @Query("SELECT * FROM log_entries ORDER BY date DESC")
    fun getAll(): LiveData<List<LogEntry>>

    @Insert
    suspend fun insert(logEntry: LogEntry)

    @Delete
    suspend fun delete(logEntry: LogEntry)
}