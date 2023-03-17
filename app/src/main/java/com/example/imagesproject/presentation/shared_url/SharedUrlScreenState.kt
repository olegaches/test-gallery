package com.example.imagesproject.presentation.shared_url

import android.location.Location
import androidx.compose.material3.SnackbarHostState

data class SharedUrlScreenState(
    val visibleBars: Boolean = true,
    val url: String = "",
    val location: Location? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
)
