package com.skripsi.absensi_app.utils.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class FusedLocation(context: Context?, activity: Activity?) {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var ctx = context
    private var act = activity

    companion object {
        var latitude: String? = null
        var longitude: String? = null
    }

    fun build() {
        // fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ctx?.let { context ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                act?.let { activity ->
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
    fun requestLocationUpdate() {
         mFusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {

            }
        }
    }

}