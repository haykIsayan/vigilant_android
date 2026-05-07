package com.example.project_vig_la.di

import com.example.project_vig_la.data.CrimeApiService
import com.example.project_vig_la.data.CrimeDtoToEntityMapper
import com.example.project_vig_la.data.CrimeRepositoryImpl
import com.example.project_vig_la.domain.CrimeRepository

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Manual DI — holds all singletons.
 *
 * Access from any Activity via:
 *   (application as CrimeMapApplication).serviceLocator
 */
object DataModule {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://data.lacity.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .build()

    val crimeApiService: CrimeApiService =
        retrofit.create(CrimeApiService::class.java)

    val crimeRepository: CrimeRepository by lazy {
        CrimeRepositoryImpl(
            crimeDtoToEntityMapper = CrimeDtoToEntityMapper(),
            crimeApiService
        )
    }
}