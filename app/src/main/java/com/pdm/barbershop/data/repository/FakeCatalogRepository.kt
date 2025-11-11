package com.pdm.barbershop.data.repository

import com.pdm.barbershop.data.core.NetworkResult
import com.pdm.barbershop.domain.model.CatalogItem
import com.pdm.barbershop.domain.model.CatalogItemType
import com.pdm.barbershop.domain.repository.CatalogRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeCatalogRepository : CatalogRepository {

    override suspend fun getServices(): NetworkResult<List<CatalogItem>> {
        // Simula atraso de rede/db
        delay(350)
        val services = listOf(
            CatalogItem(
                id = "svc-1",
                name = "Corte Masculino",
                description = "Corte clássico ou moderno.",
                price = 35.0,
                imageUrl = "https://picsum.photos/seed/svc-1/400",
                durationMinutes = 30,
                type = CatalogItemType.SERVICE
            ),
            CatalogItem(
                id = "svc-2",
                name = "Barba Completa",
                description = "Modelagem de barba + toalha quente.",
                price = 28.0,
                imageUrl = "https://picsum.photos/seed/svc-2/400",
                durationMinutes = 25,
                type = CatalogItemType.SERVICE
            ),
            CatalogItem(
                id = "svc-3",
                name = "Corte + Barba",
                description = "Combo completo com acabamento.",
                price = 55.0,
                imageUrl = "https://picsum.photos/seed/svc-3/400",
                durationMinutes = 55,
                type = CatalogItemType.SERVICE
            ),
            CatalogItem(
                id = "svc-4",
                name = "Pigmentação",
                description = "Realce de barba e cabelo.",
                price = 40.0,
                imageUrl = "https://picsum.photos/seed/svc-4/400",
                durationMinutes = 35,
                type = CatalogItemType.SERVICE
            ),
        ).shuffled(Random(System.currentTimeMillis()))
        return NetworkResult.Success(services)
    }

    override suspend fun getProducts(): NetworkResult<List<CatalogItem>> {
        delay(250)
        val products = listOf(
            CatalogItem(
                id = "prd-1",
                name = "Pomada Modeladora",
                description = "Fixação média, efeito matte.",
                price = 29.9,
                imageUrl = "https://picsum.photos/seed/prd-1/400",
                type = CatalogItemType.PRODUCT
            ),
            CatalogItem(
                id = "prd-2",
                name = "Óleo para Barba",
                description = "Hidrata e perfuma.",
                price = 39.9,
                imageUrl = "https://picsum.photos/seed/prd-2/400",
                type = CatalogItemType.PRODUCT
            ),
            CatalogItem(
                id = "prd-3",
                name = "Shampoo Anti-Resíduos",
                description = "Limpeza profunda.",
                price = 34.9,
                imageUrl = "https://picsum.photos/seed/prd-3/400",
                type = CatalogItemType.PRODUCT
            ),
        )
        return NetworkResult.Success(products)
    }
}
