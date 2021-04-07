package com.mufiid.absensi_app.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.databinding.ItemTaskBinding

class TaskAdapter(private val check: CheckListTask) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    val data = ArrayList<TaskEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTaskBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskEntity) {
            with(binding) {
                descTask.text = task.task
                if (task.isComplete == 1) {
                    checkTask.isChecked = true
                    checkTask.isEnabled = false
                }
                checkTask.setOnClickListener {
                    if (checkTask.isChecked) {
                        check.showUploadFile(task)
                        checkTask.isEnabled = false
                    }
                }
            }
        }
    }

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