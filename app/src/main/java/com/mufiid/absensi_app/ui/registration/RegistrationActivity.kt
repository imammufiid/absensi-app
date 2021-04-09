package com.mufiid.absensi_app.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ActivityRegistrationBinding
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.*

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _bind: ActivityRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        init()
    }

    private fun init() {
        _bind.btnRegister.setOnClickListener(this)
        _bind.btnLogin.setOnClickListener(this)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[RegistrationViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, {
            if (it == true) {
                _bind.progressBarRegister.visibility = View.VISIBLE
                _bind.layoutButton.visibility = View.GONE
            } else {
                _bind.progressBarRegister.visibility = View.GONE
                _bind.layoutButton.visibility = View.VISIBLE
            }
        })

        viewModel.message.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.userData.observe(this, {
            if (it != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    finish()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> finish()
            R.id.btn_register -> {
                val name = _bind.etName.text.toString()
                val email = _bind.etEmail.text.toString()
                val nik = _bind.etNik.text.toString()
                val password = _bind.etPassword.text.toString()
                val confirmPassword = _bind.etConfirmPassword.text.toString()

                when {
                    name == "" -> {
                        _bind.etName.error = "Field is required"
                    }
                    email == "" -> {
                        _bind.etEmail.error = "Field is required"
                    }
                    nik == "" -> {
                        _bind.etNik.error = "Field is required"
                    }
                    password == "" -> {
                        _bind.etPassword.error = "Field is required"
                    }
                    password != confirmPassword -> {
                        Toast.makeText(this, "password not match", Toast.LENGTH_SHORT).show()
                    }
                    else -> viewModel.registrationUser(name, nik, email, password)
                }
            }
        }
    }
}