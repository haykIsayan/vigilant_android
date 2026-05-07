package com.example.project_vig_la.vigilant.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_vig_la.domain.CrimeQueryEntity
import com.example.project_vig_la.domain.CrimeRepository
import com.example.project_vig_la.utils.LocationProvider
import com.example.project_vig_la.vigilant.CrimeCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModel(
    private val crimeRepository: CrimeRepository,
    private val locationProvider: LocationProvider
): ViewModel() {

    private val intents = MutableSharedFlow<MapIntent>(
        extraBufferCapacity = 64
    )

    private val _selectedCrime = intents
        .filterIsInstance<MapIntent.SelectCrime>()
        .map { it.crime }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private val _activeFilters = intents
        .filterIsInstance<MapIntent.ToggleFilter>()
        .scan(CrimeCategory.entries.toSet()) { current, intent ->
            if (intent.category in current) current - intent.category
            else current + intent.category
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            CrimeCategory.entries.toSet()
        )

    private val _crimes = intents
        .filterIsInstance<MapIntent.CameraMoved>()
        .flatMapLatest { intent ->
            intent.run {
                crimeRepository.getCrimes(
                    CrimeQueryEntity(
                        southLat = bounds.southwest.latitude,
                        northLat = bounds.northeast.latitude,
                        westLng = bounds.southwest.longitude,
                        eastLng = bounds.northeast.longitude
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _userLocation = intents
        .filterIsInstance<MapIntent.StreamLocation>()
        .flatMapMerge {
            locationProvider.locationUpdates()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val uiState: StateFlow<MapState> = combine(
        _crimes,
        _userLocation,
        _activeFilters,
        _selectedCrime
    ) { crimes, location, filters, selectedCrime ->

        val filteredCrimes = crimes.filter { crime ->
            CrimeCategory.fromDescription(crime.crimeDescription) in filters
        }

        MapState.Loaded(
            selectedCrime = selectedCrime,
            userLocation = location,
            bounds = null,
            filteredCrimes = filteredCrimes,
            activeFilters = filters,
            crimes = crimes

        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapState.Idle
    )

    private val _events = MutableSharedFlow<MapEvent>()
    val events: SharedFlow<MapEvent> = _events.asSharedFlow()

    fun sendIntent(intent: MapIntent) {
        intents.tryEmit(intent)
    }
}
