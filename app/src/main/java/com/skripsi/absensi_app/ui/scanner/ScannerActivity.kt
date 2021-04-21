package com.skripsi.absensi_app.ui.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.zxing.BarcodeFormat
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.databinding.ActivityScannerBinding
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScannerActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _bind: ActivityScannerBinding
    private lateinit var viewModel: ScannerViewModel
    private var codeScanner: CodeScanner? = null
    private var loading: ProgressDialog? = null
    private var latitude: String? = null
    private var longitude: String? = null

    // location
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        init()
        scanner()
        getCurrentLocation()
    }

    private fun init() {
        // view model
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[ScannerViewModel::class.java]
        observeViewModel()

        // fused location
        mFusedLocationProviderClient = getFusedLocationProviderClient(this)

        // listener
        _bind.scannerView.setOnClickListener(this)
        _bind.latitude.text = getString(R.string.latitude, getString(R.string.latitude_placeholder))
        _bind.longitude.text = getString(R.string.longitude, getString(R.string.longitude_placeholder))
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, {
            if (it) {
                loading?.apply {
                    setMessage(getString(R.string.loading))
                    setCanceledOnTouchOutside(false)
                }?.show()

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
                userPref?.token?.let { token ->
                    viewModel.attendanceCome(
                        token, userPref.id, result.text, latitude, longitude
                    )
                }
            }
        }

        codeScanner?.errorCallback = ErrorCallback {
            runOnUiThread {
                showToast("Camera initialization error: ${it.message}")
            }
        }
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1
                )
            } else {
                requestLocationUpdate()
            }
        } else {
            requestLocationUpdate()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                _bind.latitude.text = getString(R.string.latitude, location.latitude.toString())
                _bind.longitude.text = getString(R.string.longitude, location.longitude.toString())
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        }
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