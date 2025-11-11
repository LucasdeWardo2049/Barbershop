package com.pdm.barbershop.data.di

import com.pdm.barbershop.data.repository.FakeCatalogRepository
import com.pdm.barbershop.domain.repository.CatalogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCatalogRepository(): CatalogRepository {
        return FakeCatalogRepository()
    }
}