package com.example.imagesproject.presentation.theme_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.domain.type.ThemeStyleType
import com.example.imagesproject.domain.use_case.ChangeThemeStyleUseCase
import com.example.imagesproject.domain.use_case.GetAppConfigurationStreamUseCase
import com.example.imagesproject.domain.use_case.ToggleDynamicColorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class SettingsViewModelState(
    val useDynamicColors: Boolean = true,
    val themeStyle: ThemeStyleType = ThemeStyleType.FollowAndroidSystem
) {
    fun asScreenState() = ThemeSettingsScreenState(
        useDynamicColors = useDynamicColors,
        themeStyle = themeStyle
    )
}

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val getAppConfigurationStreamUseCase: GetAppConfigurationStreamUseCase,
    private val toggleDynamicColorsUseCase: ToggleDynamicColorsUseCase,
    private val changeThemeStyleUseCase: ChangeThemeStyleUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(value = SettingsViewModelState())

    val screenState = viewModelState.map { it.asScreenState() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = viewModelState.value.asScreenState()
    )

    init {
        watchAppConfigurationStream()
    }

    private fun watchAppConfigurationStream() {
        viewModelScope.launch {
            getAppConfigurationStreamUseCase().collectLatest { appConfiguration ->
                viewModelState.update { state ->
                    state.copy(
                        useDynamicColors = appConfiguration.useDynamicColors,
                        themeStyle = appConfiguration.themeStyle
                    )
                }
            }
        }
    }

    fun toggleDynamicColors() {
        viewModelScope.launch {
            toggleDynamicColorsUseCase()
        }
    }

    fun changeThemeStyle(themeStyle: ThemeStyleType) {
        viewModelScope.launch {
            changeThemeStyleUseCase(themeStyle = themeStyle)
        }
    }
}