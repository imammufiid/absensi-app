package com.skripsi.absensi_app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: BaseRepository) : ViewModel() {
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _response = MutableLiveData<UserEntity?>()
    val response: LiveData<UserEntity?> = _response

    private val _userData = MutableLiveData<UserEntity?>()
    val userData: LiveData<UserEntity?> = _userData

    fun logout(token: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.logoutUser("Bearer $token")
                when (data.value?.status) {
                    StatusResponse.SUCCESS -> _response.postValue(data.value?.body)
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

    fun getUser(token: String, userId: Int?) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.getUser("Bearer $token", userId)
                when (data.value?.status) {
                    StatusResponse.SUCCESS -> _userData.postValue(data.value?.body)
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