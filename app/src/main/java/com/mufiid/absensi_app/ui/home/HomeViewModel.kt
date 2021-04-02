package com.mufiid.absensi_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val greeting: LiveData<String> = _text

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _taskData = MutableLiveData<List<TaskEntity>?>()
    val taskData: LiveData<List<TaskEntity>?> = _taskData

    private val _attendanceToday = MutableLiveData<AttendanceEntity?>()
    val attendanceToday: LiveData<AttendanceEntity?> = _attendanceToday

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
                    StatusResponse.EMPTY -> _message.postValue(data.value?.message)
                    else -> _message.postValue(data.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }

    fun attendanceToday(
        token: String?,
        employeeId: Int?
    ) {
        viewModelScope.launch {
            try {
                val response = repo.getAttendanceToday("Bearer $token", employeeId)
                when (response.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _attendanceToday.postValue(response.value?.body)
                        _message.postValue(response.value?.message)
                    }
                    else -> _message.postValue(response.value?.message)
                }
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
            }
        }
    }

    fun markCompleteTask(
        token: String,
        idTask: Int?
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.markCompleteTask("Bearer $token", idTask)
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> _message.postValue(data.value?.message)
                    StatusResponse.EMPTY -> _message.postValue(data.value?.message)
                    else -> _message.postValue(data.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }
}