package com.pdm.barbershop.domain.repository

import com.pdm.barbershop.data.core.NetworkResult
import com.pdm.barbershop.domain.model.CatalogItem

interface CatalogRepository {
    suspend fun getServices(): NetworkResult<List<CatalogItem>>
    suspend fun getProducts(): NetworkResult<List<CatalogItem>>
}
