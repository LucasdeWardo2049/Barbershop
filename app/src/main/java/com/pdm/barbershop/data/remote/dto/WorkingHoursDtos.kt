package com.pdm.barbershop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WorkingHoursRequest(
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("dayOfWeek") val dayOfWeek: Int, // 1 = Monday, 7 = Sunday usually
    @SerializedName("startTime") val startTime: String, // "09:00:00"
    @SerializedName("endTime") val endTime: String // "18:00:00"
)
