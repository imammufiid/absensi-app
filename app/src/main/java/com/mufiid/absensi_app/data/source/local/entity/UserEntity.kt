package com.mufiid.absensi_app.data.source.local.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("is_admin")
	val isAdmin: Int? = null,

	@field:SerializedName("profile_image")
	val profileImage: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
