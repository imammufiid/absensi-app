package com.mufiid.absensi_app.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.databinding.ItemAttendanceBinding

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    val data = ArrayList<AttendanceEntity>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemAttendanceBinding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemAttendanceBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(private val binding: ItemAttendanceBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: AttendanceEntity) {
            with(binding) {
                timeIn.text = attendance.timeComes
                timeOut.text = attendance.timeGohome
                datetime.text = attendance.date
            }
        }
    }

    fun addAttendance(data: List<AttendanceEntity>?) {
        this.data.apply {
            clear()
            if (data != null) {
                addAll(data)
            }
        }
        notifyDataSetChanged()
    }
}