package com.mufiid.absensi_app.ui.scanner

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ActivityScannerBinding
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScannerActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _bind: ActivityScannerBinding
    private lateinit var viewModel: ScannerViewModel
    private var codeScanner: CodeScanner? = null
    private var loading: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        init()
        scanner()
    }

    private fun scanner() {
        codeScanner = CodeScanner(this, _bind.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }

        codeScanner?.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                val userPref = UserPref.getUserData(this)
                userPref?.token?.let { token -> viewModel.attendanceCome(token, userPref.id, result.text) }
            }
        }

        codeScanner?.errorCallback = ErrorCallback {
            runOnUiThread {
                showToast("Camera initialization error: ${it.message}")
            }
        }
    }

    private fun init() {
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[ScannerViewModel::class.java]
        observeViewModel()

        _bind.scannerView.setOnClickListener(this)
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, { it ->
            if (it) {
                loading?.let { loading ->
                    loading.setMessage(getString(R.string.loading))
                    loading.setCanceledOnTouchOutside(false)
                    loading.show()
                }

            } else {
                loading?.dismiss()
            }
        })

        viewModel.message.observe(this, {
            it?.let { message -> showToast(message) }
            GlobalScope.launch {
                delay(1000)
                finish()
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.scanner_view -> {
                codeScanner?.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }
}