package com.pdm.barbershop.domain.di

import com.pdm.barbershop.domain.repository.CatalogRepository
import com.pdm.barbershop.domain.usecase.GetProductsUseCase
import com.pdm.barbershop.domain.usecase.GetServicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetServicesUseCase(repository: CatalogRepository): GetServicesUseCase {
        return GetServicesUseCase(repository)
    }

    @Provides
    fun provideGetProductsUseCase(repository: CatalogRepository): GetProductsUseCase {
        return GetProductsUseCase(repository)
    }
}