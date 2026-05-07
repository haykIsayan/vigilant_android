package com.example.project_vig_la.vigilant.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project_vig_la.domain.CrimeRepository
import com.example.project_vig_la.utils.LocationProvider


class MapViewModelFactory(
    private val repository: CrimeRepository,
    private val locationProvider: LocationProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                repository,
                locationProvider = locationProvider
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
