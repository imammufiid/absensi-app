package com.mufiid.absensi_app.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repo: BaseRepository) : ViewModel() {

    private var _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var _attendance = MutableLiveData<List<AttendanceEntity>?>()
    val attendance: LiveData<List<AttendanceEntity>?> = _attendance

    fun allAttendance(token: String, userId: Int?) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val res = repo.getAllAttendance(token, userId)
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
}