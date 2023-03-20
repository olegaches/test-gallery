package com.example.imagesproject.domain.location_tracker

import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    fun getCurrentLocation()

    val currentLocation: StateFlow<Location?>

    suspend fun stopTracking()
}