package com.example.imagesproject.presentation.gallery_screen

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.example.imagesproject.presentation.util.convertPixelsToDp
import com.example.imagesproject.presentation.util.findCropScale
import com.example.imagesproject.presentation.util.findFinalHeight
import com.example.imagesproject.presentation.util.findFinalWidth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
            is ImageScreenEvent.OnPagerCurrentImageChange -> {
                viewModelScope.launch {
                    val stateValue = _state.value
                    val visibleInterval = stateValue.imageScreenState.visibleGridInterval
                    if(event.index > visibleInterval.second || event.index <= visibleInterval.first) {
                        onScrollToImage(event.index).join()
                    }
                    val currentElement = stateValue.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index == event.index }
                        ?: stateValue.lazyGridState.layoutInfo.visibleItemsInfo.last()
                    val cropScale = findCropScale(
                        event.painterIntrinsicSize,
                        currentElement.size.toSize()
                    )
                    val imageHeight = event.painterIntrinsicSize.height * cropScale
                    val imageWidth = event.painterIntrinsicSize.width * cropScale
                    val currentGridItemOffset = currentElement.offset
                    val height: Float
                    val width: Float
                    val imageOffset: IntOffset
                    if(imageHeight > imageWidth) {
                        height = findFinalHeight(
                            imageWidth,
                            imageHeight,
                            null
                        )
                        width = convertPixelsToDp(
                            currentElement.size.toSize().width,
                            null
                        )
                        imageOffset = currentGridItemOffset.copy(
                            y = currentGridItemOffset.y - (imageHeight - imageWidth).toInt() / 2
                        )
                    } else {
                        width = findFinalWidth(
                            imageHeight,
                            imageWidth,
                            null
                        )
                        height = convertPixelsToDp(
                            currentElement.size.toSize().height,
                            null
                        )
                        imageOffset = currentGridItemOffset.copy(
                            x = currentGridItemOffset.x - (imageWidth - imageHeight).toInt() / 2
                        )
                    }
                    val imageDpSize = DpSize(height = height.dp, width = width.dp)
                    changeCurrentGridItemOffset(imageOffset)
                    changeImageSize(imageDpSize)
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

    private fun changeImageSize(dpSize: DpSize) {
        _state.update {
            val imageState = _state.value.imageScreenState
            if(imageState.imageSize == dpSize)
                return
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    imageSize = dpSize
                )
            )
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

    private fun onScrollToImage(imageIndex: Int): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            val stateValue = _state.value
            val prevGridItemOffset = stateValue.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index == imageIndex }?.offset ?: stateValue.imageScreenState.imageOffset
            val prevImageOffset = stateValue.imageScreenState.imageOffset
            val deltaOffsetY = (prevGridItemOffset.y - prevImageOffset.y)
            stateValue.lazyGridState.scrollToItem(
                index = imageIndex,
                scrollOffset =
                if(stateValue.imageScreenState.visibleGridInterval.second <= imageIndex)
                    -stateValue.itemOffsetToScroll else {
                        0
                }
            )
            val currentGridItem = stateValue.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index == imageIndex }!!
            val imageSize = stateValue.imageScreenState.imageSize
            val newOffset: IntOffset = if(imageSize.height > imageSize.width) {
                prevImageOffset.copy(y = currentGridItem.offset.y - deltaOffsetY)
            } else {
                prevImageOffset.copy(y = currentGridItem.offset.y)
            }
            changeCurrentGridItemOffset(newOffset)
        }
    }

    private fun changeCurrentGridItemOffset(intOffset: IntOffset) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    imageScreenState = it.imageScreenState.copy(
                        imageOffset = intOffset
                    )
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
                changeImageSize(event.dpSize)
            }
            is GalleryScreenEvent.OnSaveCurrentGridItemOffset -> {
                changeCurrentGridItemOffset(event.intOffset)
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
            delay(250)
            _state.update {
                it.copy(
                    topBarTitleText = "",
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