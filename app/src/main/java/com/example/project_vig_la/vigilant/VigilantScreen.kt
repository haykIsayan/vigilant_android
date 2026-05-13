package com.example.project_vig_la.vigilant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.project_vig_la.R
import com.example.project_vig_la.domain.CrimeEntity
import com.example.project_vig_la.vigilant.state.MapEvent
import com.example.project_vig_la.vigilant.state.MapIntent
import com.example.project_vig_la.vigilant.state.MapState
import com.example.project_vig_la.vigilant.state.MapViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable
fun VigilantScreen(
    paddingValues: PaddingValues,
    mapViewModel: MapViewModel
) {

    val scope = rememberCoroutineScope()
    val locationGranted = remember { mutableStateOf(false) }

    LocationPermissionHandler(
        onGranted = {
            locationGranted.value = true
            mapViewModel.sendIntent(MapIntent.StreamLocation)
                    },
        onDenied = { /* keep going without location */ }
    )

    val context = LocalContext.current
    val losAngeles = LatLng(34.0522, -118.2437)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(losAngeles, 12f)
    }
    val uiState = mapViewModel.uiState.collectAsState()

    ObserveMapCameraPosition(
        cameraPositionState,
    ) { intent ->
        mapViewModel.sendIntent(intent)
    }

    ObserveMapEvents(
        context
    ) {
        mapViewModel.events
    }

    val activeFilters by remember {
        derivedStateOf {
            when (val state = uiState.value) {
                is MapState.Loaded -> state.activeFilters
                else -> CrimeCategory.entries.toSet()
            }
        }
    }

    val selectedCrime by remember {
        derivedStateOf {
            when (val state = uiState.value) {
                is MapState.Loaded -> state.selectedCrime
                else -> null
            }
        }
    }

    selectedCrime?.let { crime ->
        CrimeDetailSheet(
            crime = crime,
            onDismiss = {
                mapViewModel.sendIntent(MapIntent.SelectCrime(null))
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VigilantMap(
            paddingValues,
            cameraPositionState,
            uiState
        ) { crime ->
            mapViewModel.sendIntent(MapIntent.SelectCrime(crime))
        }
        UserLocationFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(22.dp),
        ) {
            val location = when (val state = uiState.value) {
                is MapState.Loaded -> state.userLocation
                else -> null
            }
            location?.let {
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(it, 15f),
                        durationMs = 800
                    )
                }
            }
        }
        CrimeFilterBar(
            activeFilters = activeFilters,
            onToggle = { category ->
                mapViewModel.sendIntent(MapIntent.ToggleFilter(category))
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(paddingValues)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun UserLocationFab(
    modifier: Modifier,
    onMoveToUserLocation: () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .rotate(45f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D0D0D))
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
            .clickable { onMoveToUserLocation() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "My Location",
            tint = Color(0xFFCC1A1A),
            modifier = Modifier
                .size(22.dp)
                .rotate(-45f) // counter-rotate so the icon stays upright
        )
    }
}

@Composable
private fun VigilantMap(
    paddingValues: PaddingValues,
    cameraPositionState: CameraPositionState,
    uiState: State<MapState>,
    onCrimeSelected: (CrimeEntity) -> Unit
) {
    val context = LocalContext.current

    val isMoving by remember {
        derivedStateOf {
            cameraPositionState.isMoving
        }
    }

    val userLocation by remember {
        derivedStateOf {
            when (val uiStateValue = uiState.value) {
                is MapState.Loading -> uiStateValue.userLocation
                is MapState.Loaded -> uiStateValue.userLocation
                else -> null
            }
        }
    }

    val crimes by remember {
        derivedStateOf {
            when (val uiStateValue = uiState.value) {
                is MapState.Loading -> uiStateValue.crimes
                is MapState.Loaded -> uiStateValue.filteredCrimes
                else -> null
            }
        }
    }

    val visibleCrimes = rememberWithCondition(
        crimes,
        !isMoving
    )
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.map_style_dark
            )
        ),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = false,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true,
        ),
    ) {
        userLocation?.apply {
            Marker(
                state = MarkerState(position = LatLng(latitude, longitude)),
                title = "User Location",
                snippet = "",
                icon = UserLocationMarkerFactory.get()
            )
        }

        visibleCrimes?.forEach { crime ->
            VigilantMarker(crime, onCrimeSelected)
        }
    }
}

@Composable
private fun VigilantMarker(
    crime: CrimeEntity,
    onCrimeSelected: (CrimeEntity) -> Unit
) {
    if (crime.lon == null || crime.lat == null) return
    key(crime.id) {
        val markerState = remember {
            MarkerState(position = LatLng(crime.lat, crime.lon))
        }
        val icon = remember {
            CrimeMarkerFactory.getMarker(
                CrimeCategory.fromDescription(crime.crimeDescription)
            )
        }
        Marker(
            state = markerState,
            title = crime.crimeDescription ?: "Unknown",
            snippet = crime.areaName ?: "",
            icon = icon,
            onClick = {
                onCrimeSelected(crime)
                true
            }
        )
    }

}

@Composable
private fun ObserveMapCameraPosition(
    cameraPositionState: CameraPositionState,
    sendIntent: (intent: MapIntent) -> Unit
) {
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .filter { isMoving -> !isMoving }
            .debounce(1000L)
            .mapNotNull { cameraPositionState.projection?.visibleRegion?.latLngBounds }
            .distinctUntilChanged()
            .collect { bounds ->
                sendIntent(MapIntent.CameraMoved(bounds))
            }
    }
}

@Composable
private fun ObserveMapEvents(
    context: Context,
    events: () -> Flow<MapEvent>,
) {
    LaunchedEffect(Unit) {
        events().collect { event ->
            when (event) {
                is MapEvent.Message -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun LocationPermissionHandler(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current

    var hasChecked = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        if (hasChecked.value) return@LaunchedEffect
        hasChecked.value = true

        val alreadyGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            onGranted()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
fun <T> rememberWithCondition(
    value: T,
    condition: Boolean
): T {
    var held by remember { mutableStateOf(value) }
    if (condition) held = value
    return held
}