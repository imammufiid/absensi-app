package com.skripsi.absensi_app.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.receiver.AttendanceReminder

class MainActivity : AppCompatActivity() {

    companion object {
        const val TIME = "10:41:00"
        const val TIME_LATE = "10:42:00"
        const val TIME_RESET = "10:43:00"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        permissionChecker()

        setReminder()

    }

    private fun setReminder() {
        // reminder time to attendance
        AttendanceReminder().apply {
            setReminderAttendanceAlarm(
                this@MainActivity,
                AttendanceReminder.ATTENDANCE,
                TIME,
                "Waktunya Absen"
            )
            setReminderLateAttendance(
                this@MainActivity,
                AttendanceReminder.LATE,
                TIME_LATE,
                "Anda Telat Absen"
            )
            resetReminderAttendanceAlarm(
                this@MainActivity,
                AttendanceReminder.RESET,
                TIME_RESET,
                "Status Absen di RESET"
            )

        }
    }

    private fun permissionChecker() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1
                )
            }
        }
    }
}