package com.example.imagesproject.presentation.images_screen

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun onImageScreenEvent(event: ImageScreenEvent) {
        when(event) {
            is ImageScreenEvent.OnAnimate -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            imageScreenState = it.imageScreenState.copy(
                                animationState = it.imageScreenState.animationState.copy(
                                    isAnimationInProgress = true,
                                    animationType = event.value
                                )
                            )
                        )
                    }
                    delay(400)
                    _state.update {
                        it.copy(
                            imageScreenState = it.imageScreenState.copy(
                                animationState = it.imageScreenState.animationState.copy(
                                    isAnimationInProgress = false,
                                )
                            )
                        )
                    }
                }
            }
            is ImageScreenEvent.OnVisibleChanged -> {
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            isVisible = event.value
                        )
                    )
                }
            }
            is ImageScreenEvent.OnPagerIndexChanged -> {
                if(_state.value.imageScreenState.imageIndex == event.value)
                    return
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            imageIndex = event.value
                        )
                    )
                }
            }
            is ImageScreenEvent.OnGridItemOffsetChange -> {
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            gridItemOffset = it.lazyGridState.layoutInfo.visibleItemsInfo[event.value].offset
                        )
                    )
                }
            }
        }
    }

    fun saveGridItemSize(size: IntSize) {
        val imageState = _state.value.imageScreenState
        if(imageState.gridItemImageSize == size)
            return
        _state.update {
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    gridItemImageSize = size
                )
            )
        }
    }

    fun saveLayoutParams(gridLayoutParams: GridLayoutParams) {
        _state.update {
            it.copy(
                gridLayoutParams = gridLayoutParams,
            )
        }
    }

    fun onImageClicked(index: Int) {
        _state.update {
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    imageIndex = index,
                    isVisible = true,
                ),
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

    fun saveGridItemOffset(offset: IntOffset) {
        _state.update {
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    gridItemOffset = offset
                )
            )
        }
    }

    private fun onNavigationBarVisibilityChange() {
        _state.update {
            it.copy(
                systemNavigationBarVisible = it.topBarVisible
            )
        }
    }

    fun savePagerIndex(index: Int) {
        _state.update {
            it.copy(
                indexToScroll = index,
            )
        }
    }

    private fun onScrollToImage(imageIndex: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val stateValue = _state.value
            if(stateValue.gridLayoutParams == null) {
                return@launch
            }
            stateValue.lazyGridState.scrollToItem(
                index = imageIndex,
                scrollOffset =
                if(stateValue.gridLayoutParams.lastFullVisibleIndex <= imageIndex)
                    -stateValue.gridLayoutParams.itemOffset else {
                        0
                }
            )
            _state.update {
                it.copy(
                    indexToScroll = null,
                )
            }
        }
    }

    fun onBackClicked() {
        val stateValue = state.value
        stateValue.indexToScroll?.let {
            onScrollToImage(it)
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    topBarVisible = false,
                    imageScreenState = it.imageScreenState.copy(
                        animationState = it.imageScreenState.animationState.copy(
                            isAnimationInProgress = true,
                            animationType = AnimationType.HIDE_ANIMATION
                        )
                    )
                )
            }
            delay(400)
            _state.update {
                it.copy(
                    imageScreenState = it.imageScreenState.copy(
                        isVisible = false,
                        animationState = it.imageScreenState.animationState.copy(
                            isAnimationInProgress = true
                        )
                    )
                )
            }
        }
    }

    private fun loadImageUrlList() {
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