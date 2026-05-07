package com.example.project_vig_la.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.Result


interface LocationProvider {
    fun locationUpdates(intervalMs: Long = 10_000L): Flow<LatLng>

    suspend fun lastLocation(): LatLng?
}

@SuppressLint("MissingPermission")
class LocationProviderImpl(context: Context): LocationProvider {

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun locationUpdates(intervalMs: Long): Flow<LatLng> = callbackFlow {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        ).setMinUpdateIntervalMillis(intervalMs / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(LatLng(location.latitude, location.longitude))
                }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun lastLocation(): LatLng? {
        return try {
            val task = client.lastLocation
            await(task)?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }
}
