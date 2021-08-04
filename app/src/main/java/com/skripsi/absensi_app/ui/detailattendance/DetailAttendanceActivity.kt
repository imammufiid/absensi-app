package com.skripsi.absensi_app.ui.detailattendance

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.android.material.snackbar.Snackbar
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.databinding.ActivityDetailAttendanceBinding
import com.skripsi.absensi_app.ui.login.LoginActivity
import com.skripsi.absensi_app.utils.pref.BasePref
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory

class DetailAttendanceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _bind: ActivityDetailAttendanceBinding
    private var dataAttendance: AttendanceEntity? = null
    private var latitude: String? = null
    private var longitude: String? = null

    private val viewModel: DetailAttendanceViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val ATTENDANCE_EXTRAS = "attendance_extras"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityDetailAttendanceBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        dataAttendance = intent.getParcelableExtra(ATTENDANCE_EXTRAS)
        init()
    }

    private fun init() {
        setDataParcelable()
        observerViewModel()
        val userPref = UserPref.getUserData(this)

        if (userPref?.isAdmin != 1) {
            _bind.detailAttendance.btnValidation.visibility = View.GONE
        }
        _bind.detailAttendance.btnViewMaps.setOnClickListener(this)
        _bind.detailAttendance.btnValidation.setOnClickListener(this)
        _bind.btnBack.setOnClickListener(this)
        _bind.detailAttendance.imageFile.setOnClickListener(this)

        userPref?.token?.let { token ->
            viewModel.getUser(token, dataAttendance?.userId)
        }

    }

    private fun observerViewModel() {

        viewModel.loading.observe(this, {
            if (it) {
                _bind.detailAttendance.progressBar.visibility = View.VISIBLE
            } else {
                _bind.detailAttendance.progressBar.visibility = View.GONE
            }
        })

        viewModel.message.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.userData.observe(this, { user ->
            if (user != null) {
                _bind.headerProfile.username.text = user.name

                // cara 2
                _bind.headerProfile.nik.text = nik(user.nik, encryptNik)

                Glide.with(this)
                    .load(user.profileImage)
                    .centerCrop()
                    .into(_bind.headerProfile.profileUser)
            }
        })

        viewModel.locationData.observe(this, { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                _bind.detailAttendance.latitude.text = location.latitude
                _bind.detailAttendance.longitude.text = location.longitude
                _bind.detailAttendance.distance.text = location.lbs
                _bind.detailAttendance.btnViewMaps.visibility = View.VISIBLE
            }
        })

        viewModel.validate.observe(this, {
            if (it != null) {
                Snackbar.make(_bind.root, it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    // high order function
    private fun nik(nik: String?, result: (String) -> String): String {
        val newNik = nik?.let { result(it) }
        return "$newNik"
    }

    private val encryptNik = { nik: String? ->
        val startNik = nik?.length?.minus(4)
        val xNik = startNik?.let {
            nik.length.minus(startNik).let { countRepeat -> "x".repeat(countRepeat) }
        }
        val concat = nik?.substring(0, nik.length - 4) + xNik
        concat
    }


    private fun setDataParcelable() {
        Log.d("ATTENDANCE_TYPE", dataAttendance.toString())
        with(_bind.detailAttendance) {
            timeIn.text = dataAttendance?.timeComes
            timeOut.text =
                if (dataAttendance?.timeGohome == "0") "00:00:00" else dataAttendance?.timeGohome

            information.text = dataAttendance?.information

        }

        if (dataAttendance?.attendanceType == 1) {
            viewModel.getLocationAttendance(UserPref.getUserData(this)?.token, dataAttendance?.id.toString())
        }

        if (dataAttendance?.fileInformation == null) {
            _bind.detailAttendance.imageFile.visibility = View.GONE
            _bind.detailAttendance.tvFile.visibility = View.VISIBLE
        } else {
            _bind.detailAttendance.tvFile.visibility = View.GONE
            val circularProgressDrawable = CircularProgressDrawable(this).apply {
                strokeWidth = 15f
                centerRadius = 30f
            }
            Glide.with(this)
                .load(dataAttendance?.fileInformation)
                .placeholder(circularProgressDrawable)
                .into(_bind.detailAttendance.imageFile)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_back -> {
                finish()
            }
            R.id.btn_view_maps -> {
                val gmmIntentUri = Uri.parse("geo:${latitude},${longitude}?")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                    resolveActivity(packageManager)
                }
                startActivity(mapIntent)
            }
            R.id.btn_validation -> {
                val userPref = UserPref.getUserData(this)
                viewModel.validate(userPref?.token, dataAttendance?.id, userPref?.isAdmin)
            }
            R.id.image_file -> {
                ImagePopup(this).apply {
                    backgroundColor = Color.BLACK
                    isFullScreen = true
                    isImageOnClickClose = true
                    initiatePopupWithGlide(dataAttendance?.fileInformation)
                }.viewPopup()
            }
        }
    }
}