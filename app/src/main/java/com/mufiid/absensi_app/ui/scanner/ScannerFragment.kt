package com.mufiid.absensi_app.ui.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.BarcodeFormat
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentScannerBinding
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ScannerFragment : Fragment() {

    private lateinit var _bind : FragmentScannerBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var viewModel: ScannerViewModel
    // location
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var latitude: String? = null
    private var longitude: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _bind = FragmentScannerBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity()

        val factory = ViewModelFactory.getInstance(activity)
        viewModel = ViewModelProvider(this, factory)[ScannerViewModel::class.java]
        observeViewModel()

        codeScanner = CodeScanner(activity, _bind.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }
        codeScanner.decodeCallback = DecodeCallback { result ->
            activity.runOnUiThread {
                val userPref = context?.let { context -> UserPref.getUserData(context) }
                userPref?.token?.let { token ->
                    viewModel.attendanceCome(
                        token, userPref.id, result.text, latitude, longitude
                    )
                }
            }
        }
        _bind.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        // fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        getCurrentLocation()
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                showToast("loading")
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            it?.let { message -> showToast(message) }
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                findNavController().navigateUp()
            }
        })
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context?.let { context ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let { activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        1
                    )
                }
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) {}
//            ScannerFragment().apply {
//                arguments = Bundle().apply {
//
//                }
//            }
    }
}