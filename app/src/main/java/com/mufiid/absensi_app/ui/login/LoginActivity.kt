package com.mufiid.absensi_app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ActivityLoginBinding
import com.mufiid.absensi_app.ui.main.MainActivity
import com.mufiid.absensi_app.ui.registration.RegistrationActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        init()
    }

    private fun init() {
        _binding.btnLogin.setOnClickListener(this)
        _binding.btnRegistration.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.btn_registration -> {
                startActivity(Intent(this, RegistrationActivity::class.java))
            }
        }
    }
}