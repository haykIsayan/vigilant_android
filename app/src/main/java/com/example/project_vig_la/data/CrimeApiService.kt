package com.example.project_vig_la.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CrimeApiService {
    @GET("resource/k7nn-b2ep.json")
    suspend fun getCrimeIncidents(
        @Query("\$where") where: String,
        @Query("\$limit") limit: Int = 50,
        @Query("\$order") order: String = "date_occ DESC"
    ): List<CrimeIncident>
}