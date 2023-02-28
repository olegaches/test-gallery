package com.example.imagesproject.presentation

import android.app.Notification
import android.app.NotificationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.domain.type.ThemeStyleType
import com.example.imagesproject.domain.use_case.GetAppConfigurationStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Data class that represents the state of activity.
 */
data class MainActivityState(
    val isLoading: Boolean,
    val useDynamicColors: Boolean,
    val themeStyle: ThemeStyleType,
    val usePowerModeSaving: Boolean,
    val currentNotification: Notification?
)

/**
 * Data class that represents the state of the view model.
 */
private data class MainViewModelState(
    val isLoading: Boolean = true,
    val useDynamicColors: Boolean = true,
    val themeStyle: ThemeStyleType = ThemeStyleType.FollowAndroidSystem,
    val usePowerModeSaving: Boolean = false,
    val currentNotification: Notification? = null
) {
    fun asActivityState() = MainActivityState(
        currentNotification = currentNotification,
        isLoading = isLoading,
        useDynamicColors = useDynamicColors,
        themeStyle = themeStyle,
        usePowerModeSaving = usePowerModeSaving,
    )
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAppConfigurationStreamUseCase: GetAppConfigurationStreamUseCase,
    private val notificationManager: NotificationManager,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(value = MainViewModelState())

    val activityState = viewModelState.map { it.asActivityState() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = viewModelState.value.asActivityState()
    )

    init {
        watchAppConfigurationStream()
    }
    private var job: Job? = null

    fun cancelNotification() {
        if(notificationManager.activeNotifications.isNotEmpty()) {
            viewModelState.update { state ->
                state.copy(
                    currentNotification = notificationManager.activeNotifications.first().notification
                )
            }
        }
        job?.cancel()
        job = viewModelScope.launch {
            delay(5000)
            notificationManager.cancel(1)
        }
    }

    fun recreateNotification() {
        job?.cancel()
        job = null
        viewModelState.value.currentNotification?.let { currentNotification ->
            notificationManager.notify(1, currentNotification)
        }
        viewModelState.update { it.copy(currentNotification = null) }
    }

    private fun watchAppConfigurationStream() {
        viewModelScope.launch {
            viewModelState.update { it.copy(isLoading = true) }
            getAppConfigurationStreamUseCase().collectLatest { appConfiguration ->
                viewModelState.update { state ->
                    state.copy(
                        isLoading = false,
                        useDynamicColors = appConfiguration.useDynamicColors,
                        usePowerModeSaving = appConfiguration.usePowerSavingMode,
                        themeStyle = appConfiguration.themeStyle
                    )
                }
            }
        }
    }
}