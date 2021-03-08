package com.mufiid.absensi_app.ui.task

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentTaskBinding
import com.mufiid.absensi_app.ui.addtask.AddTaskActivity
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class TaskFragment : Fragment(), View.OnClickListener {
    private lateinit var _bind : FragmentTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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

    private fun observerViewModel() {
        // loading
        taskViewModel.loading.observe(viewLifecycleOwner, {
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
            if (!it.isNullOrEmpty()) {
                taskAdapter.apply {
                    addTask(it)
                }
            } else {
                Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun init() {
        taskAdapter = TaskAdapter()
        setRecyclerView()
        setViewModel()

        _bind.btnAdd.setOnClickListener(this)
    }

    private fun setViewModel() {
        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token ->
            taskViewModel.getTaskData(
                token, userPref.id, null, null
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_add -> startActivity(Intent(context, AddTaskActivity::class.java))
        }
    }
}