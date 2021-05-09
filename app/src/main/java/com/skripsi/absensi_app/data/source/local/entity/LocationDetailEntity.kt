package com.skripsi.absensi_app.data.source.local.entity

import com.google.gson.annotations.SerializedName

data class LocationDetailEntity(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("attendance_id")
	val attendanceId: Int? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("lbs")
	val lbs: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

)
