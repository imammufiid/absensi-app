package com.skripsi.absensi_app.ui.detailattendance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.databinding.ActivityDetailAttendanceBinding
import com.skripsi.absensi_app.viewmodel.ViewModelFactory

class DetailAttendanceActivity : AppCompatActivity() {
    private lateinit var _bind: ActivityDetailAttendanceBinding
    private var dataAttendance: AttendanceEntity? = null

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
    }

    private fun setDataParcelable() {
        with(_bind.detailAttendance) {
            timeIn.text = dataAttendance?.timeComes
            timeOut.text = if (dataAttendance?.timeGohome == "0") "00:00:00" else dataAttendance?.timeGohome

            information.text = dataAttendance?.information
            file.text = dataAttendance?.fileInformation ?: "-"
        }
    }
}