package com.pdm.barbershop.data.di

import com.pdm.barbershop.data.repository.AuthRepositoryImpl
import com.pdm.barbershop.data.repository.FakeCatalogRepository
import com.pdm.barbershop.domain.repository.AuthRepository
import com.pdm.barbershop.domain.repository.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(impl: FakeCatalogRepository): CatalogRepository
}