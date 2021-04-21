package com.skripsi.absensi_app.utils.pref

import android.content.Context
import androidx.core.content.edit

object AuthPref {
    fun isLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return pref.getBoolean("ISLOGGEDIN", false)
    }

    fun setIsLoggedIn(context: Context, state: Boolean) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putBoolean("ISLOGGEDIN", state)
        }
    }
}