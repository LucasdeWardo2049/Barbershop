package com.pdm.barbershop.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Barber(
    val id: String,
    val name: String,
    val rating: Double, // Novo campo para a avaliação
    val imageUrl: String? = null
)

data class Service(
    val id: String,
    val name: String,
    val price: Double,
    val durationInMinutes: Int,
    val icon: ImageVector
)