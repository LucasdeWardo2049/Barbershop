package com.pdm.barbershop.data.di

import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.data.repository.AppointmentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulingModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(api: ApiService): ScheduleRepository = ScheduleRepository(api)

    @Provides
    @Singleton
    fun provideAppointmentsRepository(api: ApiService): AppointmentsRepository = AppointmentsRepository(api)
}

