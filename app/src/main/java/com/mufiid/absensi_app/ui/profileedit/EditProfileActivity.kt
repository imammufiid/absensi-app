package com.mufiid.absensi_app.ui.profileedit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.databinding.ActivityEditProfileBinding
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var _bind: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var userData: UserEntity? = null

    private var imageFromApi: String? = null
    private var currentPhotoPath: String? = null
    private var part: MultipartBody.Part? = null

    companion object {
        const val USER = "user"
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(_bind.root)

        init()
        checkPermission()
    }

    private fun init() {
        _bind.btnBack.setOnClickListener(this)
        _bind.btnSave.setOnClickListener(this)
        _bind.profileUser.setOnClickListener(this)

        userData = intent.getParcelableExtra(USER)
        setParcelable()

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[EditProfileViewModel::class.java]
        observeViewModel()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun openGalleryWithPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                pickFromGallery()
            }
        } else {
            pickFromGallery()
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
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

        Glide.with(this)
            .load(userData?.profileImage)
            .centerCrop()
            .into(_bind.profileUser)

        imageFromApi = userData?.profileImage
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this, {
            if (it == true) {
                _bind.progressBar.visibility = View.VISIBLE
            } else {
                _bind.progressBar.visibility = View.INVISIBLE
            }
        })

        viewModel.message.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.user.observe(this, {
            if (it != null) {
                _bind.etFullName.setText(it.name)
                Glide.with(this)
                    .load(it.profileImage)
                    .centerCrop()
                    .into(_bind.profileUser)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = uri?.let { uri -> contentResolver.query(uri, filePath, null, null, null) }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePath[0])
            val picturePath = columnIndex?.let { columnIndex -> cursor?.getString(columnIndex) }
            cursor?.close()
            currentPhotoPath = picturePath.toString()
            _bind.profileUser.apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_user -> openGalleryWithPermission()
            R.id.btn_back -> finish()
            R.id.btn_save -> {
                val name = _bind.etFullName.text.toString()
                if (name == "") {
                    _bind.etFullName.error = "Field is required!"
                } else {
                    val name = _bind.etFullName.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    val password = _bind.etPassword.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    val userId = UserPref.getUserData(this)?.id?.toString()
                        ?.toRequestBody("text/plaint".toMediaTypeOrNull())
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${UserPref.getUserData(this)?.token}"

                    if (currentPhotoPath != null) {
                        val pictFromBitmap = File(currentPhotoPath)
                        val reqFile = pictFromBitmap.asRequestBody("image/*".toMediaTypeOrNull())
                        part = MultipartBody.Part.createFormData("image", pictFromBitmap.name, reqFile)
                    } else {
                        if (imageFromApi.isNullOrEmpty()) {
                            Toast.makeText(this, "Image is Required", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }

                    if (_bind.etFullName.text.toString() == "") {
                        _bind.etFullName.error = "Field is required!"
                    } else {
                        viewModel.editProfile(headers, part, userId, name, password)
                    }

                }
            }
        }
    }

}