package com.pdm.barbershop.data.di

import com.pdm.barbershop.data.repository.AuthRepositoryImpl
import com.pdm.barbershop.data.repository.FakeCatalogRepository
import com.pdm.barbershop.data.repository.NotificationRepositoryImpl
import com.pdm.barbershop.data.repository.TokenRepositoryImpl
import com.pdm.barbershop.data.repository.UserRepositoryImpl
import com.pdm.barbershop.domain.repository.AuthRepository
import com.pdm.barbershop.domain.repository.CatalogRepository
import com.pdm.barbershop.domain.repository.NotificationRepository
import com.pdm.barbershop.domain.repository.TokenRepository
import com.pdm.barbershop.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
