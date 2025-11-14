package com.pdm.barbershop.data.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.pdm.barbershop.data.core.Constants
import com.pdm.barbershop.data.remote.AuthApiService
import com.pdm.barbershop.data.remote.AuthInterceptor
import com.pdm.barbershop.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val offsetDateTimeAdapter = object : TypeAdapter<OffsetDateTime>() {
            private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            override fun write(out: JsonWriter, value: OffsetDateTime?) {
                if (value == null) out.nullValue() else out.value(formatter.format(value))
            }

            override fun read(`in`: JsonReader): OffsetDateTime? {
                return if (`in`.peek() == com.google.gson.stream.JsonToken.NULL) {
                    `in`.nextNull(); null
                } else {
                    val str = `in`.nextString()
                    OffsetDateTime.parse(str)
                }
            }
        }
        return GsonBuilder()
            .registerTypeAdapter(OffsetDateTime::class.java, offsetDateTimeAdapter)
            .create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { msg -> Log.d("OkHttp", msg) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}