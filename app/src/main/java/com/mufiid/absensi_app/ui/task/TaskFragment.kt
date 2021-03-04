package com.mufiid.absensi_app.ui.task

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentTaskBinding
import com.mufiid.absensi_app.ui.addtask.AddTaskActivity

class TaskFragment : Fragment(), View.OnClickListener {
    private lateinit var _bind : FragmentTaskBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentTaskBinding.inflate(layoutInflater, container, false)
        taskViewModel =
                ViewModelProvider(this).get(TaskViewModel::class.java)

        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel.text.observe(viewLifecycleOwner, {

        })

        init()
    }

    private fun init() {
        _bind.btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_add -> startActivity(Intent(context, AddTaskActivity::class.java))
        }
    }
}