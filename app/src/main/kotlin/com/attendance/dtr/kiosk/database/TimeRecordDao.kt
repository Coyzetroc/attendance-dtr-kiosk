package com.attendance.dtr.kiosk.database

import androidx.room.*

@Dao
interface TimeRecordDao {

    @Insert
    suspend fun insert(record: TimeRecord)

    @Update
    suspend fun update(record: TimeRecord)

    @Query("SELECT * FROM time_records ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastRecord(): TimeRecord?

    @Query("SELECT * FROM time_records WHERE employeeId = :employeeId AND date = :date LIMIT 1")
    suspend fun getTodayRecord(employeeId: String, date: String): TimeRecord?

    @Query("SELECT * FROM time_records ORDER BY date DESC, createdAt DESC")
    suspend fun getAllRecords(): List<TimeRecord>

    @Query("SELECT * FROM time_records WHERE date = :date ORDER BY timeIn ASC")
    suspend fun getRecordsByDate(date: String): List<TimeRecord>

    @Delete
    suspend fun delete(record: TimeRecord)
}
