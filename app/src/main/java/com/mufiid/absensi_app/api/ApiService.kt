package com.mufiid.absensi_app.api

import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.WrappedResponse
import retrofit2.http.*

interface ApiService {

    // AUTH ---------------------------------------------------

    // LOGIN
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): WrappedResponse<UserEntity>

    // REGISTRATION
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun registerUser(
        @Field("name") name: String?,
        @Field("nik") nik: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): WrappedResponse<UserEntity>

    // LOGOUT
    @FormUrlEncoded
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String?
    ): WrappedResponse<UserEntity>

    // ATTENDANCE ---------------------------------------------------
    // SHOW ATTENDANCE
    @GET("attendance/show")
    suspend fun showAttendance(
        @Query("id_employee") idUser: Int?
    ): WrappedResponse<AttendanceEntity>

    // TASK ---------------------------------------------------
    // SHOW ALL TASK
    @GET("task")
    suspend fun showAllTask(
        @Query("user_id") idUser: Int?,
        @Query("date") date: String?,
        @Query("is_admin") isAdmin: Int?
    ): WrappedResponse<TaskEntity>
}