package com.mufiid.absensi_app.ui.profileedit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.databinding.ActivityEditProfileBinding
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var _bind: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var userData: UserEntity? = null

    companion object {
        const val USER = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        init()
    }

    private fun init() {
        _bind.btnBack.setOnClickListener(this)
        _bind.btnAdd.setOnClickListener(this)

        userData = intent.getParcelableExtra(USER)
        Log.d("USER", userData.toString())
        setParcelable()

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[EditProfileViewModel::class.java]
        observeViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun setParcelable() {
        _bind.etFullName.setText(userData?.name)

        val nik = userData?.nik
        val startXNik = nik?.length?.minus(4) // 8
        val countRepeat = startXNik?.let { startXNik -> nik.length.minus(startXNik) }
        val xNik = countRepeat?.let { countRepeat -> "x".repeat(countRepeat) }
        _bind.etNik.setText(nik?.substring(0, nik.length - 4) + xNik)
        _bind.etEmail.setText(userData?.email)
    }

    private fun observeViewModel() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> finish()
            R.id.btn_add -> {

            }
        }
    }

}