package com.skripsi.absensi_app.ui.addtask

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.databinding.ActivityAddTaskBinding
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory

class AddTaskActivity : AppCompatActivity(), View.OnClickListener {
    private var _bind: ActivityAddTaskBinding? = null
    private val bind: ActivityAddTaskBinding?
        get() = _bind

    private lateinit var viewModel: AddTaskViewModel

    private var listEmployee: MutableList<String>? = ArrayList()
    private var listIdEmployee: MutableList<Int>? = ArrayList()
    private var idEmployee: Int? = null
    private var adapterEmployee: ArrayAdapter<Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(_bind?.root)

        init()
    }

    private fun init() {
        bind?.btnBack?.setOnClickListener(this)
        bind?.btnSave?.setOnClickListener(this)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddTaskViewModel::class.java]
        observerViewModel()
        UserPref.getUserData(this)?.token?.let { token -> viewModel.getEmployee(token) }

        onEmployeeClickListener()
    }

    private fun observerViewModel() {
        viewModel.loading.observe(this, {
            if (it) {
                bind?.progressBar?.visibility = View.VISIBLE
            } else {
                bind?.progressBar?.visibility = View.GONE
            }
        })

        viewModel.message.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.userData.observe(this, { userData ->
            if (userData != null) {
                for (i in userData.indices) {
                    userData[i].name?.let { name -> listEmployee?.add(name) }
                    userData[i].id?.let { id -> listIdEmployee?.add(id) }
                }

                adapterEmployee = ArrayAdapter(
                    this,
                    R.layout.support_simple_spinner_dropdown_item,
                    listEmployee as List<*>
                )
            }
            bind?.etSearchEmployee?.setAdapter(adapterEmployee)
        })

        viewModel.taskData.observe(this, {
            if (it != null) {
                Toast.makeText(this, "Add Task Successfully", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onEmployeeClickListener() {
        bind?.etSearchEmployee?.setOnItemClickListener { _, _, i, _ ->
            idEmployee = listIdEmployee?.get(i)
            bind?.etSearchEmployee?.dismissDropDown()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> finish()
            R.id.btn_save -> {
                val desc = bind?.descTask?.text.toString()
                if (desc == "") {
                    bind?.descTask?.error = "Field is Required!"
                } else {
                    UserPref.getUserData(this)?.token?.let { token ->
                        viewModel.insertTask(
                            token, desc, idEmployee, UserPref.getUserData(this)?.isAdmin
                        )
                    }
                }
            }
        }
    }
}