package com.pdm.barbershop.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.OffsetDateTime

// Generic paged response matching backend structure
// Sort/dir are optional

data class PagedResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("sort") val sort: String? = null,
    @SerializedName("dir") val dir: String? = null
)

data class ServiceDto(
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: BigDecimal? = null,
    @SerializedName("durationMinutes") val durationMinutes: Int? = null,
    @SerializedName("isActive") val isActive: Boolean? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class UserDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: String,
    @SerializedName("clientId") val clientId: Long? = null,
    @SerializedName("barberId") val barberId: Long? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)

data class AvailabilitySlotDto(
    @SerializedName("start") val start: OffsetDateTime,
    @SerializedName("end") val end: OffsetDateTime
)

data class AppointmentDto(
    @SerializedName("appointmentId") val appointmentId: Long? = null,
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("barberName") val barberName: String? = null,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("serviceName") val serviceName: String? = null,
    @SerializedName("clientId") val clientId: Long,
    @SerializedName("startTime") val startTime: OffsetDateTime,
    @SerializedName("endTime") val endTime: OffsetDateTime? = null,
    @SerializedName("status") val status: String,
    @SerializedName("totalPrice") val totalPrice: BigDecimal? = null
)

data class AppointmentRequest(
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("clientId") val clientId: Long,
    @SerializedName("startTime") val startTime: String, // ISO 8601
    @SerializedName("status") val status: String = "SCHEDULED"
)

// Deprecated - keeping for backward compatibility during migration
@Suppress("unused")
@Deprecated("Use AppointmentRequest instead", ReplaceWith("AppointmentRequest"))
data class BookAppointmentRequest(
    @SerializedName("clientId") val clientId: Long,
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    // Must be UTC string like 2025-11-13T12:30:00Z
    @SerializedName("startTime") val startTime: String,
    @SerializedName("tz") val tz: String? = "America/Sao_Paulo"
)

@Suppress("unused")
@Deprecated("No longer used in current API version", level = DeprecationLevel.WARNING)
data class BookedAppointmentResponse(
    @SerializedName("appointmentId") val appointmentId: Long,
    @SerializedName("start") val start: OffsetDateTime,
    @SerializedName("end") val end: OffsetDateTime,
    @SerializedName("status") val status: String,
    @SerializedName("totalPrice") val totalPrice: BigDecimal
)

data class RescheduleAppointmentRequest(
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("startTime") val startTime: String, // Formato UTC: "2025-11-30T14:00:00Z"
    @SerializedName("tz") val tz: String = "America/Sao_Paulo"
)

