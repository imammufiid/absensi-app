package com.mufiid.absensi_app.data.source

import androidx.lifecycle.LiveData
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.RemoteDataSource
import com.mufiid.absensi_app.data.source.remote.response.ApiResponse
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
        token: String,
        userId: Int?,
        qrCode: String?,
        latitude: String?,
        longitude: String?,
    ): LiveData<ApiResponse<AttendanceEntity>> {
        return remoteDataSource.attendanceCome(token, userId, qrCode, latitude, longitude)
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

    override suspend fun logoutUser(token: String): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.logout(token)
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

    override suspend fun markCompleteTask(
        header: HashMap<String, String>,
        idTask: RequestBody?,
        userId: RequestBody?,
        file: MultipartBody.Part?
    ): LiveData<ApiResponse<TaskEntity>> {
        return remoteDataSource.markCompleteTask(header, idTask, userId, file)
    }

    override suspend fun getAllAttendance(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<List<AttendanceEntity>>> {
        return remoteDataSource.getAllAttendance(userId, token)
    }

    override suspend fun getAllTaskData(
        token: String,
        userId: Int?,
        date: String?,
        isAdmin: Int?
    ): LiveData<ApiResponse<List<TaskEntity>>> {
        return remoteDataSource.getAllTaskData(userId, date, isAdmin, token)
    }

    override suspend fun insertTask(
        token: String,
        userId: Int?,
        descTask: String?,
        isAdmin: Int?
    ): LiveData<ApiResponse<TaskEntity>> {
        return remoteDataSource.insertTask(token, userId, descTask, isAdmin)
    }

    override suspend fun getEmployee(token: String): LiveData<ApiResponse<List<UserEntity>>> {
        return remoteDataSource.getEmployee(token)
    }

    override suspend fun getMyPoint(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.getMyPoint(token, userId)
    }
}