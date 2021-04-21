package com.skripsi.absensi_app.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MetaResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("message")
	val message: String? = null

)
