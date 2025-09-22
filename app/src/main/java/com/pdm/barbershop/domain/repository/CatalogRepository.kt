package com.pdm.barbershop.domain.repository

import com.pdm.barbershop.domain.model.CatalogItem

interface CatalogRepository {
    suspend fun getServices(): List<CatalogItem>
    suspend fun getProducts(): List<CatalogItem>
}
