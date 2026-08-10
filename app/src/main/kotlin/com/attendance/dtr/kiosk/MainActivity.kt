package com.attendance.dtr.kiosk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.attendance.dtr.kiosk.ui.FaceDetectionActivity
import com.attendance.dtr.kiosk.ui.ReportActivity
import com.attendance.dtr.kiosk.database.TimeRecord
import com.attendance.dtr.kiosk.database.AppDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnTimeIn: Button
    private lateinit var btnTimeOut: Button
    private lateinit var btnViewReport: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvLastRecord: TextView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        btnTimeIn = findViewById(R.id.btn_time_in)
        btnTimeOut = findViewById(R.id.btn_time_out)
        btnViewReport = findViewById(R.id.btn_view_report)
        tvStatus = findViewById(R.id.tv_status)
        tvLastRecord = findViewById(R.id.tv_last_record)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        setupListeners()
        updateLastRecord()
    }

    private fun setupListeners() {
        btnTimeIn.setOnClickListener {
            val intent = Intent(this, FaceDetectionActivity::class.java)
            intent.putExtra("type", "time_in")
            startActivityForResult(intent, REQUEST_TIME_IN)
        }

        btnTimeOut.setOnClickListener {
            val intent = Intent(this, FaceDetectionActivity::class.java)
            intent.putExtra("type", "time_out")
            startActivityForResult(intent, REQUEST_TIME_OUT)
        }

        btnViewReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val employeeName = data?.getStringExtra("employee_name") ?: "Unknown"
            val employeeId = data?.getStringExtra("employee_id") ?: ""

            when (requestCode) {
                REQUEST_TIME_IN -> recordTimeIn(employeeId, employeeName)
                REQUEST_TIME_OUT -> recordTimeOut(employeeId, employeeName)
            }
        }
    }

    private fun recordTimeIn(employeeId: String, employeeName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val record = TimeRecord(
                employeeId = employeeId,
                employeeName = employeeName,
                timeIn = System.currentTimeMillis(),
                timeOut = null,
                date = getCurrentDate()
            )
            db.timeRecordDao().insert(record)

            runOnUiThread {
                tvStatus.text = "✓ Time In recorded for $employeeName"
                updateLastRecord()
            }
        }
    }

    private fun recordTimeOut(employeeId: String, employeeName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val today = getCurrentDate()
            val record = db.timeRecordDao().getTodayRecord(employeeId, today)

            if (record != null) {
                record.timeOut = System.currentTimeMillis()
                db.timeRecordDao().update(record)

                runOnUiThread {
                    tvStatus.text = "✓ Time Out recorded for $employeeName"
                    updateLastRecord()
                }
            }
        }
    }

    private fun updateLastRecord() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastRecord = db.timeRecordDao().getLastRecord()
            runOnUiThread {
                if (lastRecord != null) {
                    tvLastRecord.text = "Last: ${lastRecord.employeeName} - ${formatTime(lastRecord.timeIn)}"
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }

    companion object {
        private const val REQUEST_TIME_IN = 1
        private const val REQUEST_TIME_OUT = 2
    }
}
