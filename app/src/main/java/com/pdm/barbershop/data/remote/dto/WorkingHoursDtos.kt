package com.pdm.barbershop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WorkingHoursRequest(
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("dayOfWeek") val dayOfWeek: Int,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)

data class WorkingHourResponse(
    @SerializedName("workingHourId") val workingHourId: Long,
    @SerializedName("barberId") val barberId: Long,
    @SerializedName("dayOfWeek") val dayOfWeek: Int,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)
