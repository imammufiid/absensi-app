package com.skripsi.absensi_app.ui.detailattendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.LocationDetailEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import kotlinx.coroutines.launch

class DetailAttendanceViewModel(private val repo: BaseRepository): ViewModel() {
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _validate = MutableLiveData<String?>()
    val validate: LiveData<String?> = _validate

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _userData = MutableLiveData<UserEntity?>()
    val userData: LiveData<UserEntity?> = _userData

    private val _locationData = MutableLiveData<LocationDetailEntity?>()
    val locationData: LiveData<LocationDetailEntity?> = _locationData

    fun getUser(token: String, userId: Int?) {
        viewModelScope.launch {
            try {
                val data = repo.getUser("Bearer $token", userId)
                when (data.value?.status) {
                    StatusResponse.SUCCESS -> _userData.postValue(data.value?.body)
                    StatusResponse.EMPTY -> _message.postValue(data.value?.message)
                    else -> _message.postValue(data.value?.message)
                }
            } catch (throwable: Throwable) {
                _message.postValue(throwable.message)
            }
        }
    }

    fun getLocationAttendance(token: String?, attendanceId: String?) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.getLocationAttendance("Bearer $token", attendanceId)
                when (data.value?.status) {
                    StatusResponse.SUCCESS -> _locationData.postValue(data.value?.body)
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

    fun validate(token: String?, attendanceId: Int?, isAdmin: Int?) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.validate("Bearer $token", attendanceId, isAdmin)
                _validate.postValue(data.value?.message)
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _validate.postValue(throwable.message)
                _loading.postValue(false)
            }
        }
    }
}