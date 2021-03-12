package com.mufiid.absensi_app.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentAttendanceBinding
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class AttendanceFragment : Fragment() {
    private lateinit var _bind: FragmentAttendanceBinding
    private lateinit var attendanceViewModel: AttendanceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentAttendanceBinding.inflate(layoutInflater, container, false)
        val factory = ViewModelFactory.getInstance(requireActivity())
        attendanceViewModel =
                ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observeViewModel()
    }

    private fun init() {
        TODO("Not yet implemented")
    }


    private fun observeViewModel() {
        TODO("Not yet implemented")
    }
}