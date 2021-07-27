package com.skripsi.absensi_app.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.HttpException
import com.skripsi.absensi_app.api.ApiConfiguration
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.LocationDetailEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class RemoteDataSource(private val api: ApiConfiguration) {

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(apiConfiguration: ApiConfiguration): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(apiConfiguration)
            }
    }

    suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()

        try {
            val data = api.create().login(email, password)
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
            val data = api.create().registerUser(name, nik, email, password)
            when (data.meta?.code) {
                201 -> result.value = ApiResponse.success(data.data, data.meta.message)
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
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = api.create().logout( userId)
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
            val response = api.create().getUser( userId)
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
            val response = api.create().showAllAttendance( userId)
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
            val response = api.create().showAttendance(employeeId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
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

    suspend fun attendanceScan(
        token: HashMap<String, String>?,
        employeeId: RequestBody?,
        qrCode: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        attendanceType: RequestBody?,
        information: RequestBody?,
        fileInformation: MultipartBody.Part?,
    ): LiveData<ApiResponse<AttendanceEntity>> {
        val result = MutableLiveData<ApiResponse<AttendanceEntity>>()
        try {
            val response =
                api.create().attendanceScan( employeeId, qrCode, latitude, longitude,
                    attendanceType, information, fileInformation
                )
            when (response.meta?.code) {
                201 -> result.value = ApiResponse.success(response.data, response.meta.message)
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
                else -> {
                    result.value = ApiResponse.error("Unknown error")
                    throwable.message?.let { Log.d("UNKNOWN_ERROR", it) }
                }
            }
        }

        return result
    }

    suspend fun validate(
        token: String?,
        attendanceId: Int?,
        isAdmin: Int?,
    ): LiveData<ApiResponse<AttendanceEntity>> {
        val result = MutableLiveData<ApiResponse<AttendanceEntity>>()

        try {
            val response = api.create().validate( attendanceId, isAdmin)
            result.value = ApiResponse.success(response.data, response.meta?.message)
        } catch (throwable: Throwable) {
            Log.d("VALIDATE", throwable.toString())
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
            val response = api.create().getEmployee()
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
            val response =
                api.create().editProfile(header, imageProfile, userId, name, password)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
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

    suspend fun getLocationAttendance(
        token: String?,
        attendanceId: String?
    ): LiveData<ApiResponse<LocationDetailEntity>> {
        val result = MutableLiveData<ApiResponse<LocationDetailEntity>>()

        try {
            val data = api.create().getLocationAttendance( attendanceId)
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
}