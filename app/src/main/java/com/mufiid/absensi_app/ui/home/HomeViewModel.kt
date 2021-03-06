package com.mufiid.absensi_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
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
}