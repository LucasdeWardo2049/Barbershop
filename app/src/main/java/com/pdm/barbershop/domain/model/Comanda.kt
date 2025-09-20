package com.pdm.barbershop.domain.model

data class Comanda(
    val id: String,
    val date: String,
    val total: Double,
    val items: List<ComandaItem>
)

data class ComandaItem(
    val name: String,
    val price: Double
)