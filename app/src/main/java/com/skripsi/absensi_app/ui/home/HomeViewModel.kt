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

    fun getTaskData(
        token: String,
        userId: Int?,
        date: String?,
        isAdmin: Int?
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.getAllTaskData("Bearer $token", userId, date, isAdmin)
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> _taskData.postValue(data.value?.body)
                    StatusResponse.EMPTY -> _msgGetTaskData.postValue(data.value?.message)
                    else -> _msgGetTaskData.postValue(data.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _msgGetTaskData.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
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

    fun markCompleteTask(
        header: HashMap<String, String>,
        idTask: RequestBody?,
        userId: RequestBody?,
        file: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.markCompleteTask(header, idTask, userId, file)
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _msgGetTaskData.postValue(data.value?.message)
                        var point = _pointData.value?.toInt()
                        point = data.value?.body?.filePoint?.toInt()?.let { filePoint -> point?.plus(filePoint) }
                        _pointData.postValue(point.toString())
                    }
                    StatusResponse.EMPTY -> _msgGetTaskData.postValue(data.value?.message)
                    else -> _msgGetTaskData.postValue(data.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _msgGetTaskData.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }

    fun getMyPoint(
        token: String,
        userId: Int?
    ) {
        viewModelScope.launch {
            try {
                val data = repo.getMyPoint("Bearer $token", userId)
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _pointData.postValue(data.value?.body?.point)
                    }
                    StatusResponse.EMPTY -> _msgPoint.postValue(data.value?.message)
                    else -> _msgPoint.postValue(data.value?.message)
                }
            } catch (throwable: Throwable) {
                _msgPoint.postValue(throwable.message)
            }
        }
    }
}