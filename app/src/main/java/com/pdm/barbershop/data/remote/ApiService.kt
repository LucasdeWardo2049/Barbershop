package com.pdm.barbershop.data.remote

import com.pdm.barbershop.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    @GET("services")
    suspend fun getServices(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("active") active: Boolean? = true
    ): PagedResponse<ServiceDto>

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("sort") sort: String? = null,
        @Query("dir") dir: String? = null
    ): PagedResponse<UserDto>

    @GET("barbers/{barberId}/availability")
    suspend fun getAvailability(
        @Path("barberId") barberId: Long,
        @Query("serviceId") serviceId: Long,
        @Query("date") date: String // YYYY-MM-DD
    ): List<AvailabilitySlotDto>

    @POST("appointments/book")
    suspend fun bookAppointment(@Body req: BookAppointmentRequest): BookedAppointmentResponse

    @GET("appointments/by_client")
    suspend fun getAppointmentsByClient(@Query("clientId") clientId: Long): List<AppointmentDto>
}

