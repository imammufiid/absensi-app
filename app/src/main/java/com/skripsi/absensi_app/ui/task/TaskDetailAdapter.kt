package com.skripsi.absensi_app.ui.task

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity

class TaskDetailAdapter constructor(private val check: CheckListTask): RecyclerView.Adapter<TaskViewHolder>() {
    val data = ArrayList<TaskEntity>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun addTask(data: List<TaskEntity>?) {
        this.data.apply {
            clear()
            if (data != null) {
                addAll(data)
            }
        }
        notifyDataSetChanged()
    }

    interface CheckListTask {
        fun showUploadFile(item: TaskEntity?)
    }
}