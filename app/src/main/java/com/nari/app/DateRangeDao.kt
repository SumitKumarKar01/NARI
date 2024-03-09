package com.nari.app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DateRangeDao {
    @Insert
    suspend fun insert(dateRange: DateRange)

    @Query("SELECT * FROM date_ranges ORDER BY startDate DESC, endDate DESC")
    fun getAllDateRanges(): LiveData<List<DateRange>>
    @Query("UPDATE date_ranges SET startDate = :newStartDate WHERE id = :id")
    suspend fun updateStartDate(id: Int, newStartDate: Long)

    @Query("UPDATE date_ranges SET endDate = :newEndDate WHERE id = :id")
    suspend fun updateEndDate(id: Int, newEndDate: Long)



    @Query("DELETE FROM date_ranges WHERE id = :id")
    suspend fun deleteSpecific(id: Int)

    @Query("DELETE FROM date_ranges")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(dateRange: DateRange)
}