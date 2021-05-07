package com.skripsi.absensi_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val greeting: LiveData<String> = _text

    private val _msgGetTaskData = MutableLiveData<String?>()
    val msgGetTaskData: LiveData<String?> = _msgGetTaskData

    private val _msgAttendanceToday = MutableLiveData<String?>()
    val msgAttendanceToday: LiveData<String?> = _msgAttendanceToday

    private val _msgPoint = MutableLiveData<String?>()
    val msgPoint: LiveData<String?> = _msgPoint

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _taskData = MutableLiveData<List<TaskEntity>?>()
    val taskData: LiveData<List<TaskEntity>?> = _taskData

    private val _attendanceToday = MutableLiveData<AttendanceEntity?>()
    val attendanceToday: LiveData<AttendanceEntity?> = _attendanceToday

    private val _pointData = MutableLiveData<String?>()
    val pointData : LiveData<String?> = _pointData

    fun setGreeting(name: String?) {
        this._text.value = name
    }

    fun attendanceToday(
        token: String?,
        employeeId: Int?
    ) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val response = repo.getAttendanceToday("Bearer $token", employeeId)
                when (response.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _attendanceToday.postValue(response.value?.body)
                    }
                    else -> _msgAttendanceToday.postValue(response.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _loading.postValue(false)
                _msgAttendanceToday.postValue(throwable.message)
            }
        }
    }

}