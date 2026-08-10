package com.attendance.dtr.kiosk.services

import android.content.Context
import com.attendance.dtr.kiosk.database.TimeRecord
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class GoogleSheetsService {

    suspend fun exportToSheets(records: List<TimeRecord>, context: Context): Boolean {
        return try {
            // Load credentials from resources (credentials.json)
            val inputStream: InputStream = context.assets.open("credentials.json")
            val credentials = GoogleCredentials.fromStream(inputStream)

            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()

            val sheetsService = Sheets.Builder(
                httpTransport,
                jsonFactory,
                HttpCredentialsAdapter(credentials)
            )
                .setApplicationName("Attendance DTR Kiosk")
                .build()

            // Prepare data
            val values = mutableListOf<List<Any>>()
            values.add(listOf("Date", "Employee Name", "Employee ID", "Time In", "Time Out", "Hours Worked"))

            records.forEach { record ->
                val timeIn = formatTime(record.timeIn)
                val timeOut = if (record.timeOut != null) formatTime(record.timeOut!!) else "--"
                val hours = if (record.timeOut != null) {
                    val diff = record.timeOut!! - record.timeIn
                    String.format("%.2f", (diff / 1000 / 3600.0))
                } else {
                    "0.00"
                }

                values.add(listOf(
                    record.date,
                    record.employeeName,
                    record.employeeId,
                    timeIn,
                    timeOut,
                    hours
                ))
            }

            val body = ValueRange()
                .setValues(values as List<List<Any>>)

            val spreadsheetId = "YOUR_SPREADSHEET_ID" // Replace with actual ID
            val range = "Sheet1!A1"

            sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}
