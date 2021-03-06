package com.mufiid.absensi_app.utils.pref

import android.content.Context
import androidx.core.content.edit

object BasePref {
    fun clearPrefAuth(context: Context) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            clear()
        }
    }
}