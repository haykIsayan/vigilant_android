package com.example.project_vig_la.data

import android.os.Build
import com.example.project_vig_la.domain.CrimeEntity
import com.example.project_vig_la.domain.CrimeQueryEntity
import com.example.project_vig_la.domain.CrimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CrimeRepositoryImpl(
    private val crimeDtoToEntityMapper: CrimeDtoToEntityMapper,
    private val apiService: CrimeApiService
): CrimeRepository {
    override suspend fun getCrimes(
        query: CrimeQueryEntity
    ): Flow<List<CrimeEntity>> = flow {
        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalDate.now().minusDays(20).toString()
        } else {
            "2026-04-15"
        }
        val where = buildString {
            append("hndrdth_lat IS NOT NULL")
            append(" AND hndrdth_lat >= '${query.southLat}'")
            append(" AND hndrdth_lat <= '${query.northLat}'")
            append(" AND hndrdth_lon >= '${query.westLng}'")
            append(" AND hndrdth_lon <= '${query.eastLng}'")
            append(" AND date_occ >= '$today'")
        }
        val crimeDtos = apiService.getCrimeIncidents(
            where = where,
            limit = query.limit
        )
        val crimes = crimeDtos.map { dto ->
            crimeDtoToEntityMapper.map(dto)
        }
        emit(crimes)
    }
}