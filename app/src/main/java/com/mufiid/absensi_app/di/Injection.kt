package com.mufiid.absensi_app.di

import android.content.Context
import com.mufiid.absensi_app.data.source.remote.RemoteDataSource
import com.mufiid.absensi_app.data.source.BaseRepository

object Injection {
    fun provideRepository(context: Context): BaseRepository {
        val remoteDataSource = RemoteDataSource.getInstance()

        return BaseRepository.getInstance(remoteDataSource)
    }
}