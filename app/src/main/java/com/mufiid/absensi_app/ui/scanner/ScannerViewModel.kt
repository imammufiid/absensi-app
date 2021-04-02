package com.mufiid.absensi_app.ui.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mufiid.absensi_app.data.source.BaseRepository
import com.mufiid.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class ScannerViewModel(private val repo: BaseRepository): ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun attendanceCome(
        token: String,
        idUser: Int?,
        qrCode: String?
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.attendanceScan("Bearer $token", idUser, qrCode)
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