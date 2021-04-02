package com.mufiid.absensi_app.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.databinding.FragmentHomeBinding
import com.mufiid.absensi_app.ui.scanner.ScannerActivity
import com.mufiid.absensi_app.ui.task.TaskAdapter
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var _bind: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        homeViewModel =
            ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)

        init()
        // observe view model
        observerViewModel()
        getDateTimeToday()
    }

    private fun init() {
        taskAdapter = TaskAdapter(object : TaskAdapter.CheckListTask {
            override fun check(item: TaskEntity?) {
                context?.let { context -> UserPref.getUserData(context)?.token }?.let { token ->
                    homeViewModel.markCompleteTask(
                        token, item?.id
                    )
                }
            }
        })
        setRecyclerView()
        setViewModel()

        // set click listener
        _bind.btnScan.setOnClickListener(this)
    }

    private fun setViewModel() {
        // set greeting name
        val name = context?.let { UserPref.getUserData(it)?.name }
        homeViewModel.setGreeting(name)

        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token ->
            homeViewModel.getTaskData(
                token, userPref.id, null, null
            )

            homeViewModel.attendanceToday(
                token, userPref.id
            )
        }


    }

    private fun setRecyclerView() {
        with(_bind.rvTask) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun getDateTimeToday() {
        var date: String = ""
        date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            current.format(formatter)
        } else {
            Date().toString()
        }

        _bind.dateToday.text = date
    }

    private fun observerViewModel() {

        homeViewModel.greeting.observe(viewLifecycleOwner, {
            _bind.txtGreeting.text = getString(R.string.title_greeting_name, it)
        })

        homeViewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                _bind.progressBar.visibility = View.VISIBLE
            } else {
                _bind.progressBar.visibility = View.GONE
            }
        })

        homeViewModel.message.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.taskData.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                taskAdapter.apply {
                    addTask(it)
                }
            } else {
                _bind.tvEmpty.visibility = View.VISIBLE
                _bind.tvEmpty.text = getString(R.string.data_empty, "tugas hari ini")
            }
        })

        homeViewModel.attendanceToday.observe(viewLifecycleOwner, {
            if (it != null) {
                _bind.timeIn.text = if (it.timeComes == "0") getString(R.string.time) else it.timeComes
                _bind.timeOut.text = if (it.timeGohome == "0") getString(R.string.time) else it.timeGohome
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_scan -> {
                startActivity(Intent(context, ScannerActivity::class.java))
            }
        }
    }
}