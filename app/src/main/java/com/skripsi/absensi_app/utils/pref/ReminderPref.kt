package com.skripsi.absensi_app.utils.pref

import android.content.Context
import androidx.core.content.edit

object ReminderPref {
    fun isActiveReminder(context: Context): Boolean {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return pref.getBoolean("REMINDER", true)
    }

    fun setReminder(context: Context, state: Boolean) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putBoolean("REMINDER", state)
        }
    }
}