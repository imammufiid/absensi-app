package com.mufiid.absensi_app.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.HttpException
import com.mufiid.absensi_app.api.ApiConfig
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class RemoteDataSource {

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()

        try {
            val data = ApiConfig.instance().login(email, password)
            when (data.meta?.code) {
                200 -> result.value = ApiResponse.success(data.data)
                404 -> result.value = ApiResponse.empty(data.meta.message)
                else -> result.value = ApiResponse.failed(data.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()

        try {
            val data = ApiConfig.instance().registerUser(name, nik, email, password)
            when (data.meta?.code) {
                200 -> result.value = ApiResponse.success(data.data)
                404 -> result.value = ApiResponse.empty(data.meta.message)
                else -> result.value = ApiResponse.failed(data.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun logout(
        token: String
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = ApiConfig.instance().logout(token)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun getUser(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = ApiConfig.instance().getUser(token, userId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error(throwable.message)
            }
        }
        return result
    }

    suspend fun getAllAttendance(
        userId: Int?,
        token: String
    ): LiveData<ApiResponse<List<AttendanceEntity>>> {
        val result = MutableLiveData<ApiResponse<List<AttendanceEntity>>>()

        try {
            val response = ApiConfig.instance().showAllAttendance(token, userId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun getAttendanceToday(
        token: String,
        employeeId: Int?
    ): LiveData<ApiResponse<AttendanceEntity>> {
        val result = MutableLiveData<ApiResponse<AttendanceEntity>>()
        try {
            val response = ApiConfig.instance().showAttendance(token, employeeId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun getAllTaskData(
        userId: Int?,
        date: String? = null,
        isAdmin: Int? = null,
        token: String
    ): LiveData<ApiResponse<List<TaskEntity>>> {
        val result = MutableLiveData<ApiResponse<List<TaskEntity>>>()

        try {
            val response = ApiConfig.instance().showAllTask(token, userId, date, isAdmin)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun getEmployee(
        token: String
    ): LiveData<ApiResponse<List<UserEntity>>> {
        val result = MutableLiveData<ApiResponse<List<UserEntity>>>()

        try {
            val response = ApiConfig.instance().getEmployee(token)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun insertTask(
        token: String,
        userId: Int?,
        descTask: String?,
        isAdmin: Int?
    ): LiveData<ApiResponse<TaskEntity>> {
        val result = MutableLiveData<ApiResponse<TaskEntity>>()

        try {
            val response = ApiConfig.instance().insertTask(token, userId, descTask, isAdmin)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun editProfile(
        header: HashMap<String, String>,
        imageProfile: MultipartBody.Part?,
        userId: RequestBody?,
        name: RequestBody?,
        password: RequestBody?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = ApiConfig.instance().editProfile(header, imageProfile, userId, name, password)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    result.value = ApiResponse.error("Network Error")
                }
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> {
                    result.value = ApiResponse.error("Unknown Error")
                }
            }
        }
        return result
    }

}