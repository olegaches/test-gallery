package com.example.imagesproject.presentation.images_screen

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getImagesUrlListUseCase: GetImagesUrlListUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ImagesScreenState())
    val state = _state.asStateFlow()

    init {
        loadImageUrlList()
    }

    fun onImageClicked(index: Int, globalImageOffset: Offset) {
        _state.update {
            it.copy(
                isExpanded = true,
                currentImageIndex = index,
                clickedImageGlobalOffset = globalImageOffset,
            )
        }
    }

    fun onTopBarChangeVisibility() {
        _state.update {
            it.copy(
                topBarVisible = !it.topBarVisible
            )
        }
    }

    fun onBackClicked() {
        if(!_state.value.isExpanded)
            return
        _state.update {
            it.copy(
                topBarVisible = false,
                currentImageIndex = 0,
                currentImageUrl = null,
                isExpanded = false,
                clickedImageGlobalOffset = null,
            )
        }
    }

    fun onDeleteFailedImage(url: String) {
        val list = state.value.imagesList
        viewModelScope.launch {
            val newList = list.minus(url)
            _state.update {
                it.copy(
                    imagesList = newList
                )
            }
        }
    }

    fun loadImageUrlList() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }
            getImagesUrlListUseCase().collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                imagesList = result.data,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = result.message,
                            )
                        }
                    }
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }
}