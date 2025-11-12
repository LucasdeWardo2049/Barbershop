package com.pdm.barbershop.domain.usecase

import com.pdm.barbershop.data.core.NetworkResult
import com.pdm.barbershop.domain.model.CatalogItem
import com.pdm.barbershop.domain.repository.CatalogRepository
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(private val repository: CatalogRepository) {
    suspend operator fun invoke(): NetworkResult<List<CatalogItem>> = repository.getServices()
}

class GetProductsUseCase @Inject constructor(private val repository: CatalogRepository) {
    suspend operator fun invoke(): NetworkResult<List<CatalogItem>> = repository.getProducts()
}
