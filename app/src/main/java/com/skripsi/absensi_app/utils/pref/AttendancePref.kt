package com.skripsi.absensi_app.utils.pref

import android.content.Context
import androidx.core.content.edit

object AttendancePref {
    fun getAttendanceStatus(context: Context): Boolean {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS)
        return pref.getBoolean("ATTENDANCE", true)
    }

    fun setAttendanceStatus(context: Context, state: Boolean) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS)
        pref.edit {
            putBoolean("ATTENDANCE", state)
        }
    }
}