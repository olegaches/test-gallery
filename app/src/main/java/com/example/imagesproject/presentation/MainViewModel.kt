package com.example.imagesproject.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.domain.service.ImageService
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
    val currentUrl: String?
)

/**
 * Data class that represents the state of the view model.
 */
private data class MainViewModelState(
    val isLoading: Boolean = true,
    val useDynamicColors: Boolean = true,
    val themeStyle: ThemeStyleType = ThemeStyleType.FollowAndroidSystem,
    val usePowerModeSaving: Boolean = false,
    val currentUrl: String? = null
) {
    fun asActivityState() = MainActivityState(
        currentUrl = currentUrl,
        isLoading = isLoading,
        useDynamicColors = useDynamicColors,
        themeStyle = themeStyle,
        usePowerModeSaving = usePowerModeSaving,
    )
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAppConfigurationStreamUseCase: GetAppConfigurationStreamUseCase,
    private val imageService: ImageService
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
        val activeNotification = imageService.getCurrentUrl()
        activeNotification?.let {
            viewModelState.update { state ->
                state.copy(
                    currentUrl = it
                )
            }
        }
        job?.cancel()
        imageService.hideNotification()
        viewModelState.update { state ->
            state.copy(
                currentUrl = null
            )
        }
    }

    fun delayCancelNotification() {
        val activeNotification = imageService.getCurrentUrl()
        activeNotification?.let {
            viewModelState.update { state ->
                state.copy(
                    currentUrl = activeNotification
                )
            }
        }
        job?.cancel()
        job = viewModelScope.launch {
            delay(5000)
            imageService.hideNotification()
        }
    }

    fun recreateNotification() {
        job?.cancel()
        job = null
        viewModelState.value.currentUrl?.let { currentUrl ->
            imageService.showNotification(currentUrl)
        }
        viewModelState.update { it.copy(currentUrl = null) }
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