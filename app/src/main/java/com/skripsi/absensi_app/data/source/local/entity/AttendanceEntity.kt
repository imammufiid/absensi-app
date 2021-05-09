package com.skripsi.absensi_app.data.source.local.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttendanceEntity(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("time_comes")
	val timeComes: String? = null,

	@field:SerializedName("time_gohome")
	val timeGohome: String? = null,

	@field:SerializedName("attendance_type")
	val attendanceType: String? = null,

	@field:SerializedName("information")
	val information: String? = null,

	@field:SerializedName("file_information")
	val fileInformation: String? = null,

	@field:SerializedName("is_validate")
	val isValidate: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

) : Parcelable
