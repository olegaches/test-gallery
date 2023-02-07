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

    fun setItemOffset(imageIndex: Int, offset: Offset) {
        val newList = _state.value.imagesList.mapIndexed { index, imageItem ->
            if(index == imageIndex) {
                imageItem.copy(
                    offset = offset
                )
            }
            else {
                imageItem
            }
        }
        _state.update {
            it.copy(
                imagesList = newList
            )
        }
    }

    fun onImageClicked(index: Int) {
        _state.update {
            it.copy(
                isExpanded = true,
                currentImageIndex = index,
                openedImageLayer = true,
            )
        }
    }

    fun onBarsVisibilityChange() {
        _state.update {
            it.copy(
                topBarVisible = !it.topBarVisible,
            )
        }
        onNavigationBarVisibilityChange()
    }

    private fun onNavigationBarVisibilityChange() {
        _state.update {
            it.copy(
                systemNavigationBarVisible = it.topBarVisible
            )
        }
    }

    fun animateImage(expand: Boolean) {
        _state.update {
            it.copy(
                isExpandAnimated = expand
            )
        }
    }

    fun onHideImageLayer() {
        _state.update {
            it.copy(
                openedImageLayer = false,
            )
        }
    }

    fun onBackClicked() {
        if(!_state.value.isExpanded)
            return
        _state.update {
            it.copy(
                isExpandAnimated = false,
                topBarVisible = false,
                currentImageIndex = 0,
                currentImageUrl = null,
                isExpanded = false,
            )
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