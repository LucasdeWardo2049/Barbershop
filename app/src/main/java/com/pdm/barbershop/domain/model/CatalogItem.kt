package com.pdm.barbershop.domain.model

enum class CatalogItemType { SERVICE, PRODUCT }

data class CatalogItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val durationMinutes: Int? = null,
    val type: CatalogItemType
)
