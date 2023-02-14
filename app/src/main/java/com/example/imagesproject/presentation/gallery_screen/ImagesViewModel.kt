package com.example.imagesproject.presentation.gallery_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
                if(_state.value.imageScreenState.pagerIndex == event.value)
                    return
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            pagerIndex = event.value
                        )
                    )
                }
            }
            is ImageScreenEvent.OnGridItemOffsetChange -> {
                _state.update { imagesScreenState ->
                    imagesScreenState.copy(
                        imageScreenState = imagesScreenState.imageScreenState.copy(
                            gridItemOffset = imagesScreenState.lazyGridState.layoutInfo.visibleItemsInfo
                                .find { it.index == event.value }?.offset ?: imagesScreenState.imageScreenState.gridItemOffset
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
            is ImageScreenEvent.OnTopBarTitleTextChange -> {
                _state.update {
                    it.copy(
                        topBarTitleText = event.titleText
                    )
                }
            }
            is ImageScreenEvent.OnBackToGallery -> {
                onBackClicked()
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
                val newList = mutableListOf<Int>()
                val stateValue = _state.value
                for(i in 0 until stateValue.imagesList.size) {
                    if(!stateValue.notValidImagesIndexes.contains(i)) {
                        newList.add(i)
                    }
                }
                _state.update {
                    it.copy(
                        imageScreenState = it.imageScreenState.copy(
                            pagerIndex = newList.indexOf(event.index),
                            isVisible = true,
                            imageIndexesList = newList.toImmutableList()
                        ),
                    )
                }
            }
            is GalleryScreenEvent.OnSaveNotValidImageIndex -> {
                val imageState = _state.value
                val newList = imageState.notValidImagesIndexes + event.index
                _state.update {
                    it.copy(
                        notValidImagesIndexes = newList.toImmutableList()
                    )
                }
            }
        }
    }

    fun onBackClicked() {
        val stateValue = state.value.imageScreenState
        val visibleInterval = stateValue.visibleGridInterval
        val imageIndex = stateValue.imageIndexesList[stateValue.pagerIndex]
        if(imageIndex > visibleInterval.second || imageIndex <= visibleInterval.first) {
            onScrollToImage(imageIndex)
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
                                imagesList = result.data.toImmutableList(),
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