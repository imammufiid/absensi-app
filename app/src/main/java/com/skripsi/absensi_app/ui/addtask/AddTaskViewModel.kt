package com.skripsi.absensi_app.ui.addtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class AddTaskViewModel(private val repo: BaseRepository): ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _userData = MutableLiveData<List<UserEntity>?>()
    val userData: LiveData<List<UserEntity>?> = _userData

    private val _taskData = MutableLiveData<TaskEntity?>()
    val taskData: LiveData<TaskEntity?> = _taskData

    fun insertTask(token: String, descTask: String?, userId: Int?, isAdmin: Int?) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.insertTask("Bearer $token", userId, descTask, isAdmin)
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
            }
        }
    }
}