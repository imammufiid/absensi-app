package com.skripsi.absensi_app.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.databinding.FragmentAttendanceBinding
import com.skripsi.absensi_app.ui.detailattendance.DetailAttendanceActivity
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory

class AttendanceFragment : Fragment() {
    private lateinit var _bind: FragmentAttendanceBinding
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var attendanceAdapter: AttendanceAdapter
    private var listIdEmployee: MutableList<Int>? = ArrayList()
    private var listEmployee: MutableList<String>? = ArrayList()
    private var idEmployee: Int? = null
    private var adapterEmployee: ArrayAdapter<Any>? = null

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
        attendanceAdapter = AttendanceAdapter {
            selectedAttendance(it)
        }
        setViewModelAttendance()
        setRecyclerView()
        setEmployeeFilter()

        _bind.btnBack.setOnClickListener { findNavController().navigateUp() }
        _bind.swipeRefresh.setOnRefreshListener {
            setViewModelAttendance()
        }
    }

    private fun selectedAttendance(attendance: AttendanceEntity) {
        startActivity(Intent(context, DetailAttendanceActivity::class.java).apply {
            putExtra(DetailAttendanceActivity.ATTENDANCE_EXTRAS, attendance)
        })
    }

    private fun setEmployeeFilter() {
        val userPref = context?.let { context -> UserPref.getUserData(context) }
        if (userPref?.isAdmin == 1) {
            _bind.searchEmployee.visibility = View.VISIBLE
            userPref.token?.let { token -> viewModel.getEmployee(token) }
        } else {
            _bind.searchEmployee.visibility = View.GONE
        }


        /*  on click item employee
         *  observe data attendance
         */
        _bind.etSearchEmployee.setOnItemClickListener { _, _, i, _ ->
            idEmployee = listIdEmployee?.get(i)
            _bind.etSearchEmployee.dismissDropDown()

            userPref?.token?.let { token -> viewModel.allAttendance(token, idEmployee) }
        }
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
        if (userPref?.isAdmin == 1) {
            userPref?.token?.let { token -> viewModel.allAttendance(token, isAdmin = 1) }
        } else {
            userPref?.token?.let { token -> viewModel.allAttendance(token, userPref.id) }
        }
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
            _bind.swipeRefresh.isRefreshing = false
            if (it.isNullOrEmpty()) {
                _bind.tvEmpty.visibility = View.VISIBLE
                _bind.tvEmpty.text = getString(R.string.data_empty, "absensi")
            } else {
                _bind.tvEmpty.visibility = View.GONE
            }
            attendanceAdapter.apply {
                addAttendance(it)
            }
        })

        viewModel.userData.observe(viewLifecycleOwner, {
            if (it != null) {
                for (i in it.indices) {
                    listEmployee?.add(it[i].name.toString())
                    it[i].id?.let { idUser -> listIdEmployee?.add(idUser) }
                }

                adapterEmployee = context?.let { context ->
                    ArrayAdapter(
                        context,
                        R.layout.support_simple_spinner_dropdown_item,
                        listEmployee as List<*>
                    )
                }

                _bind.etSearchEmployee.setAdapter(adapterEmployee)
            }
        })
    }
}