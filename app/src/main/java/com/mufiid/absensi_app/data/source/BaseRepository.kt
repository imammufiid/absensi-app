package com.mufiid.absensi_app.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.RemoteDataSource
import com.mufiid.absensi_app.data.source.remote.response.ApiResponse

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

    override suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.registrationUser(name, nik, email, password)
    }

    override suspend fun logoutUser(token: String): LiveData<ApiResponse<UserEntity>> {
        return remoteDataSource.logout(token)
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

    override suspend fun getEmployee(token: String): LiveData<ApiResponse<List<UserEntity>>> {
        return remoteDataSource.getEmployee(token)
    }
}