package com.skripsi.absensi_app.data.source

import androidx.lifecycle.LiveData
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.LocationDetailEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface BaseDataSource {
    suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun logoutUser(token: String, userId: Int?): LiveData<ApiResponse<UserEntity>>
    suspend fun getUser(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun editProfile(
        header: HashMap<String, String>,
        imageProfile: MultipartBody.Part?,
        userId: RequestBody?,
        name: RequestBody?,
        password: RequestBody?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun getAllAttendance(
        token: String,
        userId: Int?,
        isAdmin: Int?
    ): LiveData<ApiResponse<List<AttendanceEntity>>>
    suspend fun getAttendanceToday(
        token: String,
        employeeId: Int?,
    ): LiveData<ApiResponse<AttendanceEntity>>
    suspend fun attendanceScan(
        token: HashMap<String, String>,
        userId: RequestBody?,
        qrCode: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        attendanceType: RequestBody?,
        information: RequestBody?,
        fileInformation: MultipartBody.Part?,
    ): LiveData<ApiResponse<AttendanceEntity>>
    suspend fun validate(
        token: String?,
        attendanceId: Int?,
        isAdmin: Int?,
    ): LiveData<ApiResponse<AttendanceEntity>>
    suspend fun getLocationAttendance(
        token: String?,
        attendanceId: String?
    ): LiveData<ApiResponse<LocationDetailEntity>>
    suspend fun getEmployee(
        token: String
    ): LiveData<ApiResponse<List<UserEntity>>>
}