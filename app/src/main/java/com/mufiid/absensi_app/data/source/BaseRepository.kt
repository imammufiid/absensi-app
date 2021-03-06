package com.mufiid.absensi_app.data.source

import androidx.lifecycle.LiveData
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
}