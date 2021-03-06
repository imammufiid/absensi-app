package com.mufiid.absensi_app.data.source

import com.mufiid.absensi_app.data.source.remote.RemoteDataSource

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
}