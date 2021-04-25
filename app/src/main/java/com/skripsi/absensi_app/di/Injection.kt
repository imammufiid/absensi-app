package com.skripsi.absensi_app.di

import android.content.Context
import com.skripsi.absensi_app.api.ApiConfiguration
import com.skripsi.absensi_app.data.source.remote.RemoteDataSource
import com.skripsi.absensi_app.data.source.BaseRepository

object Injection {
    fun provideRepository(context: Context): BaseRepository {
        val apiConfiguration = ApiConfiguration.getInstance()
        val remoteDataSource = RemoteDataSource.getInstance(apiConfiguration)

        return BaseRepository.getInstance(remoteDataSource)
    }
}