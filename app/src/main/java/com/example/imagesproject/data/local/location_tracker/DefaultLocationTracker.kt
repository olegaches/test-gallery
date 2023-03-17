package com.example.imagesproject.data.local.location_tracker

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.imagesproject.core.util.Extension
import com.example.imagesproject.domain.location_tracker.LocationTracker
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application,
    private val locationManager: LocationManager,
): LocationTracker {

    private var currentLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (lo in p0.locations) {
                // Update UI with location data
                currentLocation = lo
            }
        }
    }

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        var isGpsEnabled: Boolean = false
        Extension.trySystemAction {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        if(!hasAccessCoarseLocationPermission || !isGpsEnabled) {
            return null
        }

        locationCallback.let {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .build()
            locationClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }

        return currentLocation

//        return suspendCancellableCoroutine { cont ->
//            locationClient.lastLocation.apply {
//                if(isComplete) {
//                    if(isSuccessful) {
//                        cont.resume(result)
//                    } else {
//                        cont.resume(null)
//                    }
//                    return@suspendCancellableCoroutine
//                }
//                addOnSuccessListener {
//                    cont.resume(it)
//                }
//                addOnFailureListener {
//                    cont.resume(null)
//                }
//                addOnCanceledListener {
//                    cont.cancel()
//                }
//            }
//        }
    }

    override suspend fun stopTracking() {
        //locationClient.removeLocationUpdates()
    }
}