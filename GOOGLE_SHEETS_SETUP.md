# Google Sheets Setup Guide for Attendance DTR Kiosk

## Step-by-Step: How to Find/Create Google Sheets ID

### Method 1: Create a New Google Sheet (Recommended)

1. **Go to Google Sheets**
   - Visit: https://sheets.new
   - Or go to https://docs.google.com/spreadsheets and click "Create New"

2. **Create a Blank Spreadsheet**
   - Click the blank spreadsheet template
   - Name it: "Attendance Records" (or any name)

3. **Find Your Spreadsheet ID**
   - Look at the URL in your browser. It will look like:
   ```
   https://docs.google.com/spreadsheets/d/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7/edit#gid=0
   ```
   - The ID is the long string between `/d/` and `/edit`
   - **Copy this part:** `1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7`

4. **Setup Sheet Structure (Optional)**
   - Add headers in row 1:
   ```
   | Date | Employee Name | Employee ID | Time In | Time Out | Hours Worked |
   ```

### Method 2: Use Existing Google Sheet

1. Open your Google Sheet
2. Copy the URL from browser address bar
3. Extract the ID using this pattern:
   ```
   https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit
                                            ↑
                                      Copy this part
   ```

---

## Update Your Android App

### Option A: Hardcoded ID (Simple - for testing)

Edit `app/src/main/kotlin/com/attendance/dtr/kiosk/services/GoogleSheetsService.kt`

**Line 61:** Change this:
```kotlin
val spreadsheetId = "YOUR_SPREADSHEET_ID" // Replace with actual ID
```

**To this:**
```kotlin
val spreadsheetId = "1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7" // Your actual ID
```

### Option B: Store in SharedPreferences (Better - user configurable)

Create a new file: `SharedPreferencesHelper.kt`

```kotlin
package com.attendance.dtr.kiosk.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("dtr_prefs", Context.MODE_PRIVATE)

    fun setSpreadsheetId(id: String) {
        prefs.edit().putString("spreadsheet_id", id).apply()
    }

    fun getSpreadsheetId(): String? {
        return prefs.getString("spreadsheet_id", null)
    }

    fun setGoogleEmail(email: String) {
        prefs.edit().putString("google_email", email).apply()
    }

    fun getGoogleEmail(): String? {
        return prefs.getString("google_email", null)
    }
}
```

Then update `GoogleSheetsService.kt`:

```kotlin
// In exportToSheets function, replace line 61:
val spreadsheetId = sharedPrefs.getSpreadsheetId() 
    ?: throw Exception("Spreadsheet ID not configured")
```

---

## Google Cloud Setup (For Credentials)

### 1. Create Google Cloud Project

1. Go to: https://console.cloud.google.com/
2. Click "Select a Project" (top left)
3. Click "NEW PROJECT"
4. Name: "Attendance DTR"
5. Click "CREATE"

### 2. Enable Google Sheets API

1. Search for "Google Sheets API" in the search bar
2. Click on it
3. Click "ENABLE"

### 3. Create OAuth 2.0 Credentials

1. Go to "Credentials" (left menu)
2. Click "CREATE CREDENTIALS"
3. Select "OAuth Client ID"
4. If prompted, click "CONFIGURE CONSENT SCREEN" first
   - Choose "External"
   - Fill in app name: "Attendance DTR Kiosk"
   - Add your email
   - Click "SAVE AND CONTINUE"
5. Back to Credentials, click "CREATE CREDENTIALS" → "OAuth Client ID"
6. Application Type: **"Desktop application"**
7. Name: "Android App"
8. Click "CREATE"
9. Click the download icon to download JSON file

### 4. Add JSON to Your Project

1. Download the JSON file (from previous step)
2. Rename it to: `credentials.json`
3. Create folder: `app/src/main/assets/`
4. Copy `credentials.json` into that folder

Your file should be at:
```
attendance-dtr-kiosk/
├── app/
│   └── src/
│       └── main/
│           └── assets/
│               └── credentials.json  ← Put it here
```

---

## Testing the Setup

### 1. Build the APK
```bash
cd /path/to/attendance-dtr-kiosk
./gradlew assembleDebug
```

### 2. Install on Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Test Export
1. Open the app
2. Add a few time-in/time-out records
3. Go to "VIEW REPORT"
4. Click "EXPORT TO GOOGLE SHEETS"
5. Check your Google Sheet for the data

---

## Troubleshooting

### Issue: "File not found: credentials.json"
- **Solution:** Make sure the file is in `app/src/main/assets/` folder
- Rebuild the project: `./gradlew clean assembleDebug`

### Issue: "Invalid Spreadsheet ID"
- **Solution:** Double-check you copied the ID correctly
- It should be a long alphanumeric string (usually 44 characters)
- Don't include `/edit` or `#gid=0`

### Issue: "Permission denied" or "403 Forbidden"
- **Solution:** Share the Google Sheet with the service account email
- Find the email in your `credentials.json` file (look for `"client_email"`)
- Open your Google Sheet → Share → Add that email address

### Issue: "Network error"
- **Solution:** Make sure the device has internet connection
- Check if your internet connection allows external API calls

---

## Quick Reference

```
📊 Spreadsheet ID Format:
https://docs.google.com/spreadsheets/d/[THIS_IS_YOUR_ID]/edit

📝 Example Spreadsheet ID:
1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7

📍 Credentials File Location:
app/src/main/assets/credentials.json

🔐 Credentials Email (from JSON):
Look for "client_email" field in credentials.json
```

---

## Need Help?

Still stuck? Try:
1. Check Google Cloud Console for API errors: https://console.cloud.google.com/apis/dashboard
2. Verify your Google Sheet is shared with the service account email
3. Make sure all dependencies in `build.gradle.kts` are correctly added
4. Check the logcat for detailed error messages: `adb logcat | grep GoogleSheets`
