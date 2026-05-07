package com.example.project_vig_la.vigilant.state

import com.example.project_vig_la.domain.CrimeEntity
import com.example.project_vig_la.vigilant.CrimeCategory
import com.google.android.gms.maps.model.LatLngBounds

sealed class MapIntent {
    data class CameraMoved(val bounds: LatLngBounds) : MapIntent()
    data object StreamLocation : MapIntent()

    data class ToggleFilter(val category: CrimeCategory) : MapIntent()

    data class SelectCrime(val crime: CrimeEntity?) : MapIntent()
}