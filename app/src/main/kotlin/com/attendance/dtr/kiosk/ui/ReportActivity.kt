package com.attendance.dtr.kiosk.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.attendance.dtr.kiosk.R
import com.attendance.dtr.kiosk.database.AppDatabase
import com.attendance.dtr.kiosk.services.GoogleSheetsService
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var tvReport: TextView
    private lateinit var btnExportSheets: Button
    private lateinit var db: AppDatabase
    private val sheetsService = GoogleSheetsService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        tvReport = findViewById(R.id.tv_report)
        btnExportSheets = findViewById(R.id.btn_export_sheets)

        db = AppDatabase.getDatabase(this)

        loadReport()

        btnExportSheets.setOnClickListener {
            exportToGoogleSheets()
        }
    }

    private fun loadReport() {
        CoroutineScope(Dispatchers.IO).launch {
            val records = db.timeRecordDao().getAllRecords()
            val reportText = StringBuilder()
            reportText.append("=== ATTENDANCE REPORT ===\n\n")

            records.groupBy { it.date }.forEach { (date, dayRecords) ->
                reportText.append("Date: $date\n")
                dayRecords.forEach { record ->
                    val timeInStr = formatTime(record.timeIn)
                    val timeOutStr = if (record.timeOut != null) formatTime(record.timeOut!!) else "--:--:--"
                    val hours = if (record.timeOut != null) {
                        val diff = record.timeOut!! - record.timeIn
                        (diff / 1000 / 3600).toString()
                    } else {
                        "--"
                    }
                    reportText.append("  ${record.employeeName} | IN: $timeInStr | OUT: $timeOutStr | Hours: $hours\n")
                }
                reportText.append("\n")
            }

            runOnUiThread {
                tvReport.text = reportText.toString()
            }
        }
    }

    private fun exportToGoogleSheets() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val records = db.timeRecordDao().getAllRecords()
                val result = sheetsService.exportToSheets(records, this@ReportActivity)

                runOnUiThread {
                    if (result) {
                        Toast.makeText(
                            this@ReportActivity,
                            "Successfully exported to Google Sheets!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ReportActivity,
                            "Export failed. Check authentication.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ReportActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}
