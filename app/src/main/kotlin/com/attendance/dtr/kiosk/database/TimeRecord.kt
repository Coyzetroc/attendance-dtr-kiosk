package com.attendance.dtr.kiosk.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_records")
data class TimeRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: String,
    val employeeName: String,
    val date: String,
    val timeIn: Long,
    var timeOut: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
