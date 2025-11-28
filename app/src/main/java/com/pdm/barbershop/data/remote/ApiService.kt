package com.pdm.barbershop.data.remote

import com.pdm.barbershop.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/v1/services")
    suspend fun getServices(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("active") active: Boolean? = true
    ): PagedResponse<ServiceDto>

    // API endpoint for user management - will be used in admin features
    @GET("api/v1/users")
    suspend fun getUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("sort") sort: String? = null,
        @Query("dir") dir: String? = null
    ): PagedResponse<UserDto>

    @GET("api/v1/barbers")
    suspend fun getBarbers(): List<UserDto>

    @GET("api/v1/barbers/{barberId}/availability")
    suspend fun getAvailability(
        @Path("barberId") barberId: Long,
        @Query("serviceId") serviceId: Long,
        @Query("date") date: String // YYYY-MM-DD
    ): List<AvailabilitySlotDto>

    @POST("api/v1/appointments")
    suspend fun bookAppointment(@Body req: AppointmentRequest): AppointmentDto

    @GET("api/v1/appointments/me")
    suspend fun getMyAppointments(): List<AppointmentDto>

    @PUT("api/v1/appointments/me/{id}")
    suspend fun rescheduleAppointment(
        @Path("id") appointmentId: Long,
        @Body request: RescheduleAppointmentRequest
    ): AppointmentDto

    @DELETE("api/v1/appointments/me/{id}")
    suspend fun cancelAppointment(@Path("id") appointmentId: Long): Response<Unit>

    @Multipart
    @POST("api/v1/users/{userId}/avatar")
    suspend fun uploadAvatar(
        @Path("userId") userId: String,
        @Part avatar: MultipartBody.Part
    ): UserDto

    @GET("api/v1/users/{userId}/avatar")
    suspend fun getAvatar(@Path("userId") userId: String): ResponseBody
}
