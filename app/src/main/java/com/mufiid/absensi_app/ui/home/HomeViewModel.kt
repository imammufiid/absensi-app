package com.mufiid.absensi_app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mufiid.absensi_app.data.source.BaseRepository

class HomeViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Lorem"
    }
    val text: LiveData<String> = _text
}