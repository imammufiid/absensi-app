package com.mufiid.absensi_app.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mufiid.absensi_app.databinding.FragmentAttendanceBinding
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class AttendanceFragment : Fragment() {
    private lateinit var _bind: FragmentAttendanceBinding
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var attendanceAdapter: AttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentAttendanceBinding.inflate(layoutInflater, container, false)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel =
            ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observeViewModel()
    }

    private fun init() {
        attendanceAdapter = AttendanceAdapter()
        setViewModelAttendance()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        with(_bind.rvAttendance) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = attendanceAdapter
        }
    }

    private fun setViewModelAttendance() {
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token -> viewModel.allAttendance(token, userPref.id) }
    }


    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                _bind.progressBar.visibility = View.VISIBLE
            } else {
                _bind.progressBar.visibility = View.GONE
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.attendance.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show()
            }
            attendanceAdapter.apply {
                addAttendance(it)
            }
        })
    }
}