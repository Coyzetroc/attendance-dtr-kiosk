# Attendance DTR Kiosk

An Android application for time tracking with face detection, Google Sheets integration, and comprehensive reporting.

## Features

- ✅ **Face Detection** - Uses ML Kit for real-time face detection
- ✅ **Time In/Out Tracking** - Simple tap-to-record interface with facial verification
- ✅ **Local Database** - Room database for offline storage
- ✅ **Google Sheets Export** - Automatic sync to Google Sheets
- ✅ **Attendance Reports** - View detailed attendance records
- ✅ **Kiosk UI** - Large buttons and clear interface for kiosk deployment

## Requirements

- Android 7.0 (API 24) or higher
- Camera permissions
- Internet connection (for Google Sheets export)
- Google API credentials

## Setup Instructions

### 1. Google Sheets Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable the Google Sheets API
4. Create OAuth 2.0 credentials (Desktop application)
5. Download credentials and save as `credentials.json` in `app/src/main/assets/`
6. Create a Google Sheet and note the Spreadsheet ID
7. Replace `YOUR_SPREADSHEET_ID` in `GoogleSheetsService.kt`

### 2. Build APK

```bash
# Development build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

APK files will be in `app/build/outputs/apk/`

### 3. Installation

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. **Time In**: Click "TIME IN" button, confirm face detection
2. **Time Out**: Click "TIME OUT" button, confirm face detection  
3. **View Report**: Click "VIEW REPORT" to see attendance records
4. **Export**: Click "EXPORT TO GOOGLE SHEETS" to sync data

## Project Structure

```
app/src/main/
├── kotlin/com/attendance/dtr/kiosk/
│   ├── MainActivity.kt          # Main UI
│   ├── ui/
│   │   ├── FaceDetectionActivity.kt   # Face detection camera
│   │   └── ReportActivity.kt          # Report viewing
│   ├── database/
│   │   ├── TimeRecord.kt        # Data model
│   │   ├── TimeRecordDao.kt     # Database operations
│   │   └── AppDatabase.kt       # Room database
│   └── services/
│       └── GoogleSheetsService.kt    # Google Sheets API
├── res/
│   └── layout/
│       ├── activity_main.xml
│       ├── activity_face_detection.xml
│       └── activity_report.xml
└── AndroidManifest.xml
```

## Next Steps

- [ ] Implement employee database
- [ ] Add eye detection/gaze tracking
- [ ] Add biometric authentication
- [ ] Implement multiple spreadsheet support
- [ ] Add offline sync queue
- [ ] Implement admin dashboard

## License

MIT License
