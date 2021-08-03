package com.skripsi.absensi_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.absensi_app.data.source.BaseRepository
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.remote.response.StatusResponse
import com.skripsi.absensi_app.utils.helper.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val greeting: LiveData<String> = _text

    private val _msgAttendanceToday = MutableLiveData<Event<String?>>()
    val msgAttendanceToday: LiveData<Event<String?>> = _msgAttendanceToday

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _attendanceToday = MutableLiveData<AttendanceEntity?>()
    val attendanceToday: LiveData<AttendanceEntity?> = _attendanceToday


    fun setGreeting(name: String?) {
        this._text.value = name
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
                    else -> _msgAttendanceToday.postValue(Event(response.value?.message))
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _loading.postValue(false)
                _msgAttendanceToday.postValue(Event(throwable.message))
            }
        }
    }

    fun attendanceCome(
        token: HashMap<String, String>,
        userId: RequestBody?,
        qrCode: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        attendanceType: RequestBody?,
        information: RequestBody?,
        fileInformation: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = repo.attendanceScan(token, userId, qrCode, latitude, longitude, attendanceType, information, fileInformation)
                when(data.value?.status) {
                    StatusResponse.SUCCESS -> {
                        _attendanceToday.postValue(data.value?.body)
                        _msgAttendanceToday.postValue(Event(data.value?.message))
                    }
                    StatusResponse.EMPTY -> _msgAttendanceToday.postValue(Event(data.value?.message))
                    else -> _msgAttendanceToday.postValue(Event(data.value?.message))
                }
                _loading.postValue(false)
            } catch (throwable: Throwable) {
                _msgAttendanceToday.postValue(Event(throwable.message))
                _loading.postValue(false)
            }
        }
    }

}