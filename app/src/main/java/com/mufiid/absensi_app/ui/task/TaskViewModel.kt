package com.mufiid.absensi_app.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class TaskViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _taskData = MutableLiveData<List<TaskEntity>?>()
    val taskData: LiveData<List<TaskEntity>?> = _taskData

    private val _userData = MutableLiveData<List<UserEntity>?>()
    val userData: LiveData<List<UserEntity>?> = _userData

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