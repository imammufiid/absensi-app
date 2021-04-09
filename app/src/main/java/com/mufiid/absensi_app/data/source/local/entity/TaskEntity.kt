package com.mufiid.absensi_app.data.source.local.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskEntity(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("task")
	val task: String? = null,

	@field:SerializedName("is_complete")
	val isComplete: Int? = null,

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("datetime")
	val datetime: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	) : Parcelable
