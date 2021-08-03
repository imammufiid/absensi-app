package com.skripsi.absensi_app.data.source

import androidx.lifecycle.LiveData
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.LocationDetailEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.RemoteDataSource
import com.skripsi.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class BaseRepository private constructor(
    private val remoteDataSource: RemoteDataSource
): BaseDataSource {

    companion object {
        @Volatile
        private var instance: BaseRepository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource
        ): BaseRepository =
            instance ?: synchronized(this) {
                instance ?: BaseRepository(remoteDataSource)
            }
    }

    override suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.loginUser(email, password)
    }

    override suspend fun attendanceScan(
        token: HashMap<String, String>,
        userId: RequestBody?,
        qrCode: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        attendanceType: RequestBody?,
        information: RequestBody?,
        fileInformation: MultipartBody.Part?,
    ): LiveData<ApiResponse<AttendanceEntity>> {
        return remoteDataSource.attendanceScan(token, userId, qrCode, latitude, longitude, attendanceType, information, fileInformation)
    }

    override suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.registrationUser(name, nik, email, password)
    }

    override suspend fun getAttendanceToday(
        token: String,
        employeeId: Int?
    ): LiveData<ApiResponse<AttendanceEntity>> {
        return remoteDataSource.getAttendanceToday(token, employeeId)
    }

    override suspend fun logoutUser(token: String, userId: Int?): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.logout(token, userId)
    }

    override suspend fun validate(
        token: String?,
        attendanceId: Int?,
        isAdmin: Int?
    ): LiveData<ApiResponse<AttendanceEntity>> {
        return remoteDataSource.validate(token, attendanceId, isAdmin)
    }

    override suspend fun getUser(token: String, userId: Int?): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.getUser(token, userId)
    }

    override suspend fun editProfile(
        header: HashMap<String, String>,
        imageProfile: MultipartBody.Part?,
        userId: RequestBody?,
        name: RequestBody?,
        password: RequestBody?
    ): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.editProfile(header, imageProfile, userId, name, password)
    }

    override suspend fun getAllAttendance(
        token: String,
        userId: Int?,
        isAdmin: Int?
    ): LiveData<ApiResponse<List<AttendanceEntity>>> {
        return remoteDataSource.getAllAttendance(userId, token, isAdmin)
    }

    override suspend fun getLocationAttendance(
        token: String?,
        attendanceId: String?
    ): LiveData<ApiResponse<LocationDetailEntity>> {
        return remoteDataSource.getLocationAttendance(token, attendanceId)
    }

    override suspend fun getEmployee(token: String): LiveData<ApiResponse<List<UserEntity>>> {
        return remoteDataSource.getEmployee(token)
    }
}