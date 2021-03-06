package com.mufiid.absensi_app.utils.pref

import android.content.Context
import androidx.core.content.edit
import com.mufiid.absensi_app.data.source.local.entity.UserEntity

object UserPref {
    fun getUserData(context: Context): UserEntity? {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return UserEntity().apply {
            email = pref.getString("EMAIL", "")
            name = pref.getString("NAME", "")
            id = pref.getInt("ID_USER", 0)
            token = pref.getString("TOKEN", "")
        }
    }

    fun setUserData(context: Context, user: UserEntity?) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putString("EMAIL", user?.email)
            putString("NAME", user?.name)
            putString("TOKEN", user?.token)
            user?.id?.let { putInt("ID_USER", it) }
        }
    }
}