package com.skripsi.absensi_app.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.databinding.ItemAttendanceBinding

class AttendanceAdapter(
    private val onClick: (AttendanceEntity) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    val data = ArrayList<AttendanceEntity>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemAttendanceBinding =
            ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemAttendanceBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: AttendanceEntity) {
            with(binding) {
                itemView.setOnClickListener {
                    onClick(attendance)
                }
                // attendance time
                timeIn.text = if (attendance.timeComes == "0") "00:00:00" else attendance.timeComes
                timeOut.text =
                    if (attendance.timeGohome == "0") "00:00:00" else attendance.timeGohome
                datetime.text = attendance.date

                // attendance type
                attendanceType.text = when (attendance.attendanceType?.toInt()) {
                    1 -> "Masuk"
                    2 -> "Ijin"
                    3 -> "Sakit"
                    else -> "-"
                }

                // validation
                when (attendance.isValidate?.toInt()) {
                    1 -> {
                        isValidation.text = "Sudah Divalidasi"
                        isValidation.setBackgroundResource(R.drawable.bg_text_validation)
                    }
                    0 -> {
                        isValidation.text = "Belum Divalidasi"
                        isValidation.setBackgroundResource(R.drawable.bg_text_not_validation)
                    }
                }
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