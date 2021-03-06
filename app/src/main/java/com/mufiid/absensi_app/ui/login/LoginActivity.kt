package com.mufiid.absensi_app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ActivityLoginBinding
import com.mufiid.absensi_app.ui.main.MainActivity
import com.mufiid.absensi_app.ui.registration.RegistrationActivity
import com.mufiid.absensi_app.utils.pref.AuthPref
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding : ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        init()
    }

    private fun init() {
        _binding.btnLogin.setOnClickListener(this)
        _binding.btnRegistration.setOnClickListener(this)

        // view model
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        observeViewModel()

        // check session
        if (AuthPref.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun observeViewModel() {
        // message
        viewModel.message.observe(this, {
            showToast(it)
        })

        // loading
        viewModel.loading.observe(this, {
            if (it) {
                _binding.layoutButton.visibility = View.GONE
                _binding.progressBarLogin.visibility = View.VISIBLE
            } else {
                _binding.layoutButton.visibility = View.VISIBLE
                _binding.progressBarLogin.visibility = View.GONE
            }
        })

        // user data
        viewModel.userData.observe(this, {
            // set pref
            if (it != null) {
                AuthPref.setIsLoggedIn(this, true)
                UserPref.setUserData(this, it)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }


        })
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> {
                val email = _binding.etEmail.text.toString()
                val password = _binding.etPassword.text.toString()

                if (email == "" || password == "") {
                    showToast("Field is required!")
                } else {
                    viewModel.loginUser(email, password)
                }
            }
            R.id.btn_registration -> {
                startActivity(Intent(this, RegistrationActivity::class.java))
            }
        }
    }
}