package com.mufiid.absensi_app.data.source.remote.response

class ApiResponse<T>(val status: StatusResponse, val body: T?,val message: String?) {
    companion object {
        fun <T> success(body: T?, message: String? = ""): ApiResponse<T> = ApiResponse(StatusResponse.SUCCESS, body, message)
        fun <T> empty(message: String?): ApiResponse<T> = ApiResponse(StatusResponse.EMPTY, null, message)
        fun <T> error(message: String?): ApiResponse<T> = ApiResponse(StatusResponse.ERROR, null, message)
        fun <T> failed(message: String?): ApiResponse<T> = ApiResponse(StatusResponse.FAILED, null, message)
    }
}