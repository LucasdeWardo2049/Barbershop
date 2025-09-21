package com.pdm.barbershop.data.repository

import com.pdm.barbershop.domain.repository.CatalogRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeCatalogRepository : CatalogRepository {

    override suspend fun getServices(): List<com.pdm.barbershop.domain.model.CatalogItem> {
        // Simula atraso de rede/db
        delay(350)
        return listOf<com.pdm.barbershop.domain.model.CatalogItem>(
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "svc-1",
                name = "Corte Masculino",
                description = "Corte clássico ou moderno.",
                price = 35.0,
                durationMinutes = 30,
                type = com.pdm.barbershop.domain.model.CatalogItemType.SERVICE
            ),
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "svc-2",
                name = "Barba Completa",
                description = "Modelagem de barba + toalha quente.",
                price = 28.0,
                durationMinutes = 25,
                type = com.pdm.barbershop.domain.model.CatalogItemType.SERVICE
            ),
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "svc-3",
                name = "Corte + Barba",
                description = "Combo completo com acabamento.",
                price = 55.0,
                durationMinutes = 55,
                type = com.pdm.barbershop.domain.model.CatalogItemType.SERVICE
            ),
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "svc-4",
                name = "Pigmentação",
                description = "Realce de barba e cabelo.",
                price = 40.0,
                durationMinutes = 35,
                type = com.pdm.barbershop.domain.model.CatalogItemType.SERVICE
            ),
        ).shuffled(Random(System.currentTimeMillis()))
    }

    override suspend fun getProducts(): List<com.pdm.barbershop.domain.model.CatalogItem> {
        delay(250)
        return listOf<com.pdm.barbershop.domain.model.CatalogItem>(
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "prd-1",
                name = "Pomada Modeladora",
                description = "Fixação média, efeito matte.",
                price = 29.9,
                type = com.pdm.barbershop.domain.model.CatalogItemType.PRODUCT
            ),
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "prd-2",
                name = "Óleo para Barba",
                description = "Hidrata e perfuma.",
                price = 39.9,
                type = com.pdm.barbershop.domain.model.CatalogItemType.PRODUCT
            ),
            com.pdm.barbershop.domain.model.CatalogItem(
                id = "prd-3",
                name = "Shampoo Anti-Resíduos",
                description = "Limpeza profunda.",
                price = 34.9,
                type = com.pdm.barbershop.domain.model.CatalogItemType.PRODUCT
            ),
        )
    }
}
