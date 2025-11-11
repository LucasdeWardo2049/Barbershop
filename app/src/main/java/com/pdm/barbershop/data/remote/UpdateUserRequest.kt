package com.pdm.barbershop.data.remote

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String // Adicionado
)