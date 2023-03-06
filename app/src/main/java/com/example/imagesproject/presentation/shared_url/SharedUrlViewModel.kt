package com.example.imagesproject.presentation.shared_url

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.domain.use_case.AddImageUrlToRoomDbUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedUrlViewModel @Inject constructor(
    private val addImageUrlToRoomDbUseCase: AddImageUrlToRoomDbUseCase
): ViewModel() {
    private val _state = MutableStateFlow(SharedUrlScreenState())
    val state = _state.asStateFlow()

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