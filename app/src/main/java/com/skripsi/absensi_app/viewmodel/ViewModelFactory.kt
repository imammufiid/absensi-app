package com.skripsi.absensi_app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.di.Injection
import com.skripsi.absensi_app.ui.attendance.AttendanceViewModel
import com.skripsi.absensi_app.ui.detailattendance.DetailAttendanceViewModel
import com.skripsi.absensi_app.ui.home.HomeViewModel
import com.skripsi.absensi_app.ui.login.LoginViewModel
import com.skripsi.absensi_app.ui.profile.ProfileViewModel
import com.skripsi.absensi_app.ui.profileedit.EditProfileViewModel
import com.skripsi.absensi_app.ui.registration.RegistrationViewModel
import com.skripsi.absensi_app.ui.scanner.ScannerViewModel

class ViewModelFactory private constructor(private val repo: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repo) as T
            modelClass.isAssignableFrom(AttendanceViewModel::class.java) -> AttendanceViewModel(repo) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repo) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repo) as T
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> EditProfileViewModel(repo) as T
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> RegistrationViewModel(repo) as T
            modelClass.isAssignableFrom(ScannerViewModel::class.java) -> ScannerViewModel(repo) as T
            modelClass.isAssignableFrom(DetailAttendanceViewModel::class.java) -> DetailAttendanceViewModel(repo) as T
            else -> throw Throwable("Unknown ViewModel Class: ${modelClass.name}")
        }
    }
}