package com.pdm.barbershop.domain.usecase

import com.pdm.barbershop.domain.model.CatalogItem
import com.pdm.barbershop.domain.repository.CatalogRepository

class GetServicesUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(): List<CatalogItem> = repository.getServices()
}

class GetProductsUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(): List<CatalogItem> = repository.getProducts()
}

