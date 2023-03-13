package com.example.imagesproject.presentation.shared_url

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.imagesproject.domain.use_case.AddImageUrlToRoomDbUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedUrlViewModel @Inject constructor(
    private val addImageUrlToRoomDbUseCase: AddImageUrlToRoomDbUseCase,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
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
    }

    fun onBarsVisibilityChange() {
        _state.update {
            it.copy(
                visibleBars = !it.visibleBars
            )
        }
    }

    fun onSaveImage(url: String) {
        viewModelScope.launch {
            addImageUrlToRoomDbUseCase(url)
        }
    }
}