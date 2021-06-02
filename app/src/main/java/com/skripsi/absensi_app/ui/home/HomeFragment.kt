package com.skripsi.absensi_app.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.databinding.FragmentHomeBinding
import com.skripsi.absensi_app.ui.ijinattendance.BottomSheetIjinAttendance
import com.skripsi.absensi_app.ui.sickAttendance.BottomSheetSickAttendance
import com.skripsi.absensi_app.utils.helper.FusedLocation
import com.skripsi.absensi_app.utils.pref.AttendancePref
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var _bind: FragmentHomeBinding
    private var filePath: String? = null
    private var part: MultipartBody.Part? = null

    // location
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var latitude: String? = null
    private var longitude: String? = null

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        // observe view model
        observerViewModel()
        getDateTimeToday()
    }

    private fun init() {
        setViewModel()

        _bind.mainMenu.menuSick.setOnClickListener(this)
        _bind.mainMenu.menuIjin.setOnClickListener(this)
        _bind.mainMenu.menuListAttendance.setOnClickListener(this)
        _bind.detailAttendance.information.text = getString(R.string.information, "-")

        // fused location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        getCurrentLocation()
    }

    private fun setViewModel() {
        // set greeting name
        val name = context?.let { UserPref.getUserData(it)?.name }
        homeViewModel.setGreeting(name)

        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token ->
            homeViewModel.attendanceToday(
                token, userPref.id
            )
        }
    }

    internal var buttonListenerIjinAttendance: BottomSheetIjinAttendance.ButtonListener =
        object : BottomSheetIjinAttendance.ButtonListener {
            override fun send(information: String?) {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer ${context?.let { context -> UserPref.getUserData(context)?.token }}"
                val requestUserId =
                    context?.let { context ->
                        UserPref.getUserData(context)?.id.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                val requestLatitude = latitude?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestLongitude = longitude?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestInfo = information?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestAttendanceType = BottomSheetIjinAttendance.ATTENDANCE_TYPE.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                homeViewModel.attendanceCome(
                    headers,
                    requestUserId,
                    null,
                    requestLatitude,
                    requestLongitude,
                    requestAttendanceType,
                    requestInfo
                )
            }
        }

    internal var buttonListener: BottomSheetSickAttendance.ButtonListener =
        object : BottomSheetSickAttendance.ButtonListener {
            override fun pickFile() {
                openFile()
            }

            override fun send(information: String?) {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer ${context?.let { context -> UserPref.getUserData(context)?.token }}"
                val requestUserId =
                    context?.let { context ->
                        UserPref.getUserData(context)?.id.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                val requestLatitude = latitude?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestLongitude = longitude?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestInfo = information?.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestAttendanceType = BottomSheetSickAttendance.ATTENDANCE_TYPE.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                if (filePath != null) {
                    val file = File(filePath)
                    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    part = MultipartBody.Part.createFormData("file_information", file.name, reqFile)
                } else {
                    Toast.makeText(context, "Please upload your file/photo", Toast.LENGTH_SHORT).show()
                    return
                }

                homeViewModel.attendanceCome(
                    headers,
                    requestUserId,
                    null,
                    requestLatitude,
                    requestLongitude,
                    requestAttendanceType,
                    requestInfo,
                    part
                )
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == BottomSheetSickAttendance.PICK_FILE) {
            val uri = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                uri?.let { uri -> context?.contentResolver?.query(uri, filePath, null, null, null) }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePath[0])
            val picturePath = columnIndex?.let { columnIndex -> cursor.getString(columnIndex) }
            cursor?.close()

            this.filePath = picturePath
            val splitFileName = this.filePath?.split("/")
            val bundle = Bundle().apply {
                putString(BottomSheetSickAttendance.FILENAME, splitFileName?.last())
            }
            BottomSheetSickAttendance().apply {
                arguments = bundle
            }.show(
                childFragmentManager,
                BottomSheetSickAttendance.TAG
            )
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, BottomSheetSickAttendance.PICK_FILE)
    }

    private fun openFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context?.let { context ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                activity?.let { activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            } else {
                pickFile()
            }
        } else {
            pickFile()
        }
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
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        }
    }

    private fun getDateTimeToday() {
        var date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            current.format(formatter)
        } else {
            Date().toString()
        }

        _bind.detailAttendance.dateToday.text = date
    }

    private fun observerViewModel() {

        homeViewModel.greeting.observe(viewLifecycleOwner, {
            _bind.txtGreeting.text = getString(R.string.title_greeting_name, it)
        })

        homeViewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                _bind.detailAttendance.progressBar.visibility = View.VISIBLE
            } else {
                _bind.detailAttendance.progressBar.visibility = View.GONE
            }
        })

        homeViewModel.msgAttendanceToday.observe(viewLifecycleOwner, {
            if (it != null) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        homeViewModel.attendanceToday.observe(viewLifecycleOwner, {
            if (it != null) {
                _bind.detailAttendance.information.text = when (it.attendanceType?.toInt()) {
                    BottomSheetIjinAttendance.ATTENDANCE_TYPE -> getString(R.string.information, "Ijin")
                    BottomSheetSickAttendance.ATTENDANCE_TYPE -> getString(R.string.information, "Sakit")
                    else -> getString(R.string.information, "Masuk")
                }
                when (it.isValidate?.toInt()) {
                    1 -> {
                        _bind.detailAttendance.isValidation.text = getString(R.string.validation)
                        _bind.detailAttendance.isValidation.setBackgroundResource(R.drawable.bg_text_validation)
                    }
                    0 -> {
                        _bind.detailAttendance.isValidation.text = getString(R.string.not_validation)
                        _bind.detailAttendance.isValidation.setBackgroundResource(R.drawable.bg_text_not_validation)
                    }
                }

                _bind.detailAttendance.timeIn.text =
                    if (it.timeComes == "0") getString(R.string.time) else it.timeComes
                _bind.detailAttendance.timeOut.text =
                    if (it.timeGohome == "0") getString(R.string.time) else it.timeGohome

                // set status attendance
                context?.let { it1 -> AttendancePref.setAttendanceStatus(it1, false) }
            }
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.menu_ijin -> {
                BottomSheetIjinAttendance().show(
                    childFragmentManager,
                    BottomSheetIjinAttendance.TAG
                )
            }
            R.id.menu_sick -> {
                BottomSheetSickAttendance().show(
                    childFragmentManager,
                    BottomSheetSickAttendance.TAG
                )
            }
            R.id.menu_list_attendance -> {
                findNavController().navigate(R.id.navigation_attendance)
            }
        }
    }
}