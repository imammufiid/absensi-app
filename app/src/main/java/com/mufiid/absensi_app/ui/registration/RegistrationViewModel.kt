package com.mufiid.absensi_app.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.ApiResponse
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?> = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _userData = MutableLiveData<UserEntity?>()
    val userData : LiveData<UserEntity?> = _userData

    fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(true)
                val response = repo.registrationUser(name, nik, email, password)
                when (response.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _userData.postValue(response.value?.body)
                        _message.postValue(response.value?.message)
                    }
                    StatusResponse.EMPTY -> _message.postValue(response.value?.message)
                    else -> _message.postValue(response.value?.message)
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }
}