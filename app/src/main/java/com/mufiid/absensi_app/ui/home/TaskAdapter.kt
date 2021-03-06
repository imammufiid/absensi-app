package com.mufiid.absensi_app.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.databinding.ItemTaskBinding

class TaskAdapter() : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    val data = ArrayList<TaskEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder {
        val itemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTaskBinding)
    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskEntity) {
            with(binding) {
                descTask.text = task.task

                checkTask.setOnClickListener {
                    if (checkTask.isChecked) {
                        Log.d("CHECKED", "checked ${task.id}")
                    } else {
                        Log.d("CHECKED", "unchecked ${task.id}")
                    }
                }
            }
        }
    }

    fun addTask(data: List<TaskEntity>) {
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

}