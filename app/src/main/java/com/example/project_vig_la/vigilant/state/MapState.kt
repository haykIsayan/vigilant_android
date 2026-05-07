package com.example.project_vig_la.vigilant.state

import com.example.project_vig_la.domain.CrimeEntity
import com.example.project_vig_la.vigilant.CrimeCategory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

sealed interface CrimeState {
    data object Failed: CrimeState
    data class Loading(
        val crimes: List<CrimeEntity>
    ): CrimeState
    data class Loaded(
        val crimes: List<CrimeEntity>
    ): CrimeState

}


sealed interface MapState {

    data object Idle : MapState
    data class Loading(
        val userLocation: LatLng,
        val bounds: LatLngBounds,
        val crimes: List<CrimeEntity>
    ) : MapState
    data class Loaded(
        val selectedCrime: CrimeEntity? = null,
        val userLocation: LatLng?,
        val bounds: LatLngBounds? = null,
        val activeFilters: Set<CrimeCategory> = CrimeCategory.entries.toSet(),
        val filteredCrimes: List<CrimeEntity>,
        val crimes: List<CrimeEntity>
    ) : MapState
    data class Error(val message: String) : MapState
}