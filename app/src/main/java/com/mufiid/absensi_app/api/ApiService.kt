package com.mufiid.absensi_app.api

import com.mufiid.absensi_app.data.source.local.entity.AttendanceEntity
import com.mufiid.absensi_app.data.source.local.entity.TaskEntity
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
import com.mufiid.absensi_app.data.source.remote.response.WrappedListResponse
import com.mufiid.absensi_app.data.source.remote.response.WrappedResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Header("Authorization") token: String?,
        @Field("user_id") userId: Int? = null
    ): WrappedResponse<UserEntity>

    // GET USER
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String?,
        @Query("id") userId: Int? = null
    ): WrappedResponse<UserEntity>

    // EDIT USER
    @Multipart
    @POST("user/save")
    suspend fun editProfile(
        @HeaderMap token: Map<String, String>?,
        @Part imageProfile: MultipartBody.Part?,
        @Part("id") userId: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("password") password: RequestBody?
    ): WrappedResponse<UserEntity>

    // ATTENDANCE ---------------------------------------------------
    // SHOW ATTENDANCE
    @GET("attendance/show")
    suspend fun showAttendance(
        @Header("Authorization") token: String?,
        @Query("id_employee") idUser: Int?
    ): WrappedResponse<AttendanceEntity>

    // SHOW ALL ATTENDANCE
    @GET("attendance/")
    suspend fun showAllAttendance(
        @Header("Authorization") token: String?,
        @Query("user_id") idUser: Int?
    ): WrappedListResponse<AttendanceEntity>

    // TASK ---------------------------------------------------
    // SHOW ALL TASK
    @GET("task")
    suspend fun showAllTask(
        @Header("Authorization") token: String?,
        @Query("user_id") idUser: Int?,
        @Query("date") date: String?,
        @Query("is_admin") isAdmin: Int?
    ): WrappedListResponse<TaskEntity>

    // INSERT ALL TASK
    @FormUrlEncoded
    @POST("task/store")
    suspend fun insertTask(
        @Header("Authorization") token: String?,
        @Field("user_id") idUser: Int?,
        @Field("task") descTask: String?,
        @Field("is_admin") isAdmin: Int?
    ): WrappedResponse<TaskEntity>

    // EMPLOYEE ---------------------------------------------------
    // SHOW ALL EMPLOYEE
    @GET("employee")
    suspend fun getEmployee(
        @Header("Authorization") token: String?,
    ): WrappedListResponse<UserEntity>
}