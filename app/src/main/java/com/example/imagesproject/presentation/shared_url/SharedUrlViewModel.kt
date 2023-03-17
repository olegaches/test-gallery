package com.example.imagesproject.presentation.shared_url

import android.content.Intent
import android.location.LocationManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.imagesproject.core.util.Extension
import com.example.imagesproject.domain.location_tracker.LocationTracker
import com.example.imagesproject.domain.type.LocationParams
import com.example.imagesproject.domain.use_case.AddImageUrlToRoomDbUseCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedUrlViewModel @Inject constructor(
    private val addImageUrlToRoomDbUseCase: AddImageUrlToRoomDbUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val locationTracker: LocationTracker,
    private val locationManager: LocationManager,
): ViewModel() {
    private val permissionSharedFlow = MutableSharedFlow<Boolean>()
//    private val _openGpsRequest = MutableSharedFlow<Boolean>()
//    val openGpsRequest = _openGpsRequest.asSharedFlow()

    private val _state = MutableStateFlow(SharedUrlScreenState())
    val state = _state.asStateFlow()
    init {
        savedStateHandle.get<Intent>(NavController.KEY_DEEP_LINK_INTENT)?.getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
            _state.update {
                it.copy(
                    url = url
                )
            }
        }

        viewModelScope.launch {
            permissionSharedFlow.collect { isSuccess ->
                if(isSuccess) {
                    getLocation()
                }
            }
        }
    }

    fun onBarsVisibilityChange() {
        _state.update {
            it.copy(
                visibleBars = !it.visibleBars
            )
        }
    }

    fun onPermissionResult(isSuccess: Boolean) {
        viewModelScope.launch {
            permissionSharedFlow.emit(isSuccess)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun onSwitchToggle(permissionState: PermissionState) {
        viewModelScope.launch {
            if(!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            } else {
                getLocation()
            }
        }
    }

    private suspend fun getLocation() {
        locationTracker.getCurrentLocation()?.let { location ->
            _state.update {
                it.snackbarHostState.showSnackbar(message = location.toString())
                it.copy(
                    location = location,
                )
            }
        }
    }

    fun onSaveImage(url: String) {
        viewModelScope.launch {
            val location = state.value.location
            val locationParsed = location?.let { LocationParams(it.latitude, location.longitude) }
            addImageUrlToRoomDbUseCase(url, locationParsed)
        }
    }
}