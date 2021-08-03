package com.skripsi.absensi_app.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repo: BaseRepository) : ViewModel() {

    private var _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var _attendance = MutableLiveData<List<AttendanceEntity>?>()
    val attendance: LiveData<List<AttendanceEntity>?> = _attendance

    private val _userData = MutableLiveData<List<UserEntity>?>()
    val userData: LiveData<List<UserEntity>?> = _userData

    fun allAttendance(token: String, userId: Int? = null, isAdmin: Int? = null) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val res = repo.getAllAttendance("Bearer $token", userId, isAdmin)
                when (res.value?.status) {
                    StatusResponse.SUCCESS -> _attendance.postValue(res.value?.body)
                    StatusResponse.EMPTY -> _message.postValue(res.value?.message)
                    else -> _message.postValue(res.value?.message)
                }
                _loading.postValue(false)
            } catch (t: Throwable) {
                _message.postValue(t.message)
                _loading.postValue(false)
            }
        }
    }

    fun getEmployee(token: String) {
        viewModelScope.launch {
            try {
                val data = repo.getEmployee("Bearer $token")
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> _userData.postValue(data.value?.body)
                    StatusResponse.EMPTY -> _message.postValue(data.value?.message)
                    else -> _message.postValue(data.value?.message)
                }
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }
}