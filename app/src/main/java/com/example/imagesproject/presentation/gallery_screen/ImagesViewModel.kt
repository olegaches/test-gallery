package com.example.imagesproject.presentation.gallery_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
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
                _state.update { imagesScreenState ->
                    imagesScreenState.copy(
                        imageScreenState = imagesScreenState.imageScreenState.copy(
                            gridItemOffset = imagesScreenState.lazyGridState.layoutInfo.visibleItemsInfo
                                .find { it.index ==  event.value }?.offset ?: imagesScreenState.imageScreenState.gridItemOffset
                        )
                    )
                }
            }
            is ImageScreenEvent.OnGridItemSizeChange -> {
                _state.update {
                    val imageState = _state.value.imageScreenState
                    if(imageState.gridItemImageSize == event.value)
                        return
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            gridItemImageSize = event.value
                        )
                    )
                }
            }
            is ImageScreenEvent.OnBarsVisibilityChange -> {
                onBarsVisibilityChange()
            }
        }
    }

    private fun onBarsVisibilityChange() {
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

//    private fun savePagerIndex(index: Int) {
//        _state.update {
//            it.copy(
//                indexToScroll = index,
//            )
//        }
//    }

    private fun onScrollToImage(imageIndex: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val stateValue = _state.value
            stateValue.lazyGridState.scrollToItem(
                index = imageIndex,
                scrollOffset =
                if(stateValue.imageScreenState.visibleGridInterval.second <= imageIndex)
                    -stateValue.itemOffsetToScroll else {
                        0
                }
            )
            onImageScreenEvent(ImageScreenEvent.OnGridItemOffsetChange(imageIndex))
            _state.update {
                it.copy(
                    indexToScroll = null,
                )
            }
        }
    }

    fun onGalleryScreenEvent(event: GalleryScreenEvent) {
        when(event) {
            is GalleryScreenEvent.OnSaveGridItemOffsetToScroll -> {
                _state.update {
                    it.copy(
                        itemOffsetToScroll = event.yOffset
                    )
                }
            }
            is GalleryScreenEvent.OnSaveGridItemSize -> {
                onImageScreenEvent(ImageScreenEvent.OnGridItemSizeChange(event.intSize))
            }
            is GalleryScreenEvent.OnSaveCurrentGridItemOffset -> {
                onImageScreenEvent(ImageScreenEvent.OnGridItemOffsetChange(event.index))
            }
            is GalleryScreenEvent.OnSaveGridVisibleInterval -> {
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            visibleGridInterval = Pair(event.startIndex, event.endIndex)
                        )
                    )
                }
            }
            is GalleryScreenEvent.OnImageClick -> {
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            imageIndex = event.index,
                            isVisible = true,
                        ),
                    )
                }
            }
        }
    }

    fun onBackClicked() {
        val stateValue = state.value.imageScreenState
        val visibleInterval = stateValue.visibleGridInterval
        if(stateValue.imageIndex > visibleInterval.second || stateValue.imageIndex <= visibleInterval.first) {
            onScrollToImage(stateValue.imageIndex)
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