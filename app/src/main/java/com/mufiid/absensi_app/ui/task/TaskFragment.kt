package com.mufiid.absensi_app.ui.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentTaskBinding
import com.mufiid.absensi_app.ui.addtask.AddTaskActivity
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private lateinit var _bind: FragmentTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private var listEmployee: MutableList<String>? = ArrayList()
    private var listIdEmployee: MutableList<Int>? = ArrayList()
    private var idEmployee : Int? = null
    private var adapterEmployee: ArrayAdapter<Any>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentTaskBinding.inflate(layoutInflater, container, false)
        val factory = ViewModelFactory.getInstance(requireActivity())
        taskViewModel =
            ViewModelProvider(this, factory).get(TaskViewModel::class.java)

        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        // observe view model
        observerViewModel()
    }

    private fun init() {
        taskAdapter = TaskAdapter()
        setViewModelTask()
        setRecyclerView()
        onEmployeeClickListener()

        _bind.btnFilter.setOnClickListener(this)
        _bind.btnCheck.setOnClickListener(this)
        _bind.btnClear.setOnClickListener(this)
        _bind.btnAdd.setOnClickListener(this)
        _bind.searchDate.setOnClickListener(this)
    }

    private fun observerViewModel() {
        // loading
        taskViewModel.loading.observe(viewLifecycleOwner, {
            Log.d("LOADING", it.toString())
            if (it) {
                _bind.progressBar.visibility = View.VISIBLE
            } else {
                _bind.progressBar.visibility = View.GONE
            }
        })
        // message
        taskViewModel.message.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        // get all task
        taskViewModel.taskData.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                _bind.tvEmpty.visibility = View.VISIBLE
                _bind.tvEmpty.text = getString(R.string.data_empty, "tugas")
            }
            taskAdapter.apply {
                addTask(it)
            }
        })

        // get employee
        taskViewModel.userData.observe(viewLifecycleOwner, {
            if (it != null) {
                listEmployee?.clear()
                listIdEmployee?.clear()
                for (i in it.indices) {
                    listEmployee?.add(it[i].name.toString())
                    it[i].id?.let { id -> listIdEmployee?.add(id) }
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

    private fun setViewModelTask(date: String? = null, idEmployee: Int? = null) {
        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        var userId = userPref?.id
        if (idEmployee != null) {
            userId = idEmployee
        }
        val isAdmin = userPref?.isAdmin
        userPref?.token?.let { token ->
            taskViewModel.getTaskData(
                token, userId, date, isAdmin
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

    private fun onEmployeeClickListener() {
        _bind.etSearchEmployee.setOnItemClickListener { _, _, i, _ ->
            idEmployee = listIdEmployee?.get(i)
            _bind.etSearchEmployee.dismissDropDown()
        }
    }


    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(v: DatePicker?, y: Int, m: Int, d: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(y, m, d)
        val date = calendar.time

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val dateStr = dateFormat.format(date)
        _bind.searchDate.setText(dateStr)
    }

    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val datePick = context?.let { context ->
            DatePickerDialog(
                context,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePick?.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_filter -> {
                _bind.btnFilter.visibility = View.INVISIBLE
                _bind.btnCheck.visibility = View.VISIBLE
                _bind.btnClear.visibility = View.VISIBLE
                _bind.searchDate.visibility = View.VISIBLE

                if (context?.let { UserPref.getUserData(it) }?.isAdmin == 1) {
                    _bind.searchEmployee.visibility = View.VISIBLE
                    val userPref = context?.let { UserPref.getUserData(it) }
                    userPref?.token?.let { token ->
                        taskViewModel.getEmployee(
                            token
                        )
                    }
                }
            }
            R.id.btn_check -> {
                val date = _bind.searchDate.text.toString()
                setViewModelTask(date = date, idEmployee)
            }
            R.id.btn_clear -> {
                _bind.btnFilter.visibility = View.VISIBLE
                _bind.btnCheck.visibility = View.INVISIBLE
                _bind.btnClear.visibility = View.INVISIBLE
                _bind.searchDate.visibility = View.GONE

                if (context?.let { UserPref.getUserData(it) }?.isAdmin == 1) {
                    _bind.searchEmployee.visibility = View.GONE
                }
            }
            R.id.btn_add -> startActivity(Intent(context, AddTaskActivity::class.java))
            R.id.search_date -> showCalendar()
        }
    }
}