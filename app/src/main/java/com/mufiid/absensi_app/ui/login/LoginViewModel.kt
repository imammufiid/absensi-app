package com.mufiid.absensi_app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: BaseRepository) :ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _userData = MutableLiveData<UserEntity>()
    val userData: LiveData<UserEntity> = _userData

    fun loginUser(email: String?, password: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(true)
                val data = repo.loginUser(email, password)
                when (data.value?.status) {
                    StatusResponse.SUCCESS -> {
                        data.value?.body.let {
                            _userData.postValue(it)
                        }
                    }
                    StatusResponse.EMPTY -> {
                        data.value?.message.let {
                            _message.postValue(it)
                        }
                    }
                    else -> {
                        data.value?.message.let {
                            _message.postValue(it)
                        }
                    }
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }

}