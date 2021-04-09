package com.mufiid.absensi_app.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.databinding.ItemTaskBinding

class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun inflate(parent: ViewGroup): TaskViewHolder {
            val view = ItemTaskBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return TaskViewHolder(view)
        }
    }
    fun bind(task: TaskEntity) {
        with(binding) {
            descTask.text = task.task
            if (task.isComplete == 1) {
                checkTask.isChecked = true
                checkTask.isEnabled = false
            }
            itemView.setOnClickListener {
                // check.showUploadFile(task)
            }
        }
    }

}