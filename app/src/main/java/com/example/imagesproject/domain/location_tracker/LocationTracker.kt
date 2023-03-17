package com.example.imagesproject.domain.location_tracker

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?

    suspend fun stopTracking()
}