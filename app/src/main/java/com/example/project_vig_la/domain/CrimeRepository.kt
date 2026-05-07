package com.example.project_vig_la.domain

import kotlinx.coroutines.flow.Flow

interface CrimeRepository {
    suspend fun getCrimes(
        query: CrimeQueryEntity
    ): Flow<List<CrimeEntity>>
}