package com.example.imagesproject.presentation.gallery_screen.full_screen_image

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesproject.domain.service.ImageService
import com.example.imagesproject.domain.use_case.DeleteImageUrlFromRoomDbUseCase
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullScreenImageViewModel @Inject constructor(
    private val imageService: ImageService,
    private val savedStateHandle: SavedStateHandle,
    private val deleteImageUrlFromRoomDbUseCase: DeleteImageUrlFromRoomDbUseCase,
): ViewModel() {
    init {}

    private val _state = MutableStateFlow(FullScreenState())
    val state = _state.asStateFlow()

    fun onImageScreenEvent(event: ImageScreenEvent) {
        when(event) {
            is ImageScreenEvent.OnAnimate -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            animationState = it.animationState.copy(
                                isAnimationInProgress = true,
                                animationType = event.value
                            )
                        )
                    }
                    delay(400)
                    _state.update {
                        it.copy(
                            animationState = it.animationState.copy(
                                isAnimationInProgress = false,
                            )
                        )
                    }
                }
            }
            is ImageScreenEvent.OnVisibleChanged -> {
                _state.update {
                    it.copy(
                        isVisible = event.value
                    )
                }
            }
            is ImageScreenEvent.OnPagerIndexChanged -> {
                if(_state.value.pagerIndex == event.value)
                    return
                _state.update {
                    it.copy(
                        pagerIndex = event.value
                    )
                }
            }
            is ImageScreenEvent.OnPagerCurrentImageChange -> {
                changeImageSize(event.painterIntrinsicSize)
            }
            is ImageScreenEvent.OnBarsVisibilityChange -> {
                onBarsVisibilityChange()
            }
            is ImageScreenEvent.OnTopBarTitleTextChange -> {
                changeTopBarText(event.topBarText)
            }
            is ImageScreenEvent.OnBackToGallery -> {
                //onBackClicked() TODO
            }
            is ImageScreenEvent.OnShowNotification -> {
                notifyOpenedImage(event.imageUrl)
            }
            is ImageScreenEvent.OnHideNotification -> {
                onHideNotification()
            }
            is ImageScreenEvent.OnDeleteImageUrl -> {
                deleteImageUrl(event.imageUrl)
            }
        }
    }

    private fun deleteImageUrl(imageUrl: String) {
        viewModelScope.launch {
            deleteImageUrlFromRoomDbUseCase(imageUrl)
        }
    }

    private fun notifyOpenedImage(imageUrl: String) {
        imageService.showNotification(imageUrl)
    }

    private fun changeImageSize(size: Size) {
        _state.update {
            val imageState = _state.value
            if(imageState.painterIntrinsicSize.toSize() == size)
                return
            it.copy(
                painterIntrinsicSize = ParcelableSize(size.width, size.height)
            )
        }
    }

    private fun changeTopBarText(text: String) {
        _state.update {
            it.copy(
                topBarText = text,
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

    private fun onHideNotification() {
        imageService.hideNotification()
    }

    private fun changeCurrentGridItemOffset(intOffset: IntOffset) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    imageOffset = ParcelableIntOffset(intOffset.x, intOffset.y)
                )
            }
        }
    }

    private suspend fun onScrollToImage(imageIndex: Int, isScrollToEnd: Boolean) {
//        val stateValue = _state.value TODO
//        stateValue.lazyGridState.scrollToItem(
//            index = imageIndex,
//            scrollOffset =
//            if(isScrollToEnd)
//                -stateValue.itemOffsetToScroll else {
//                0
//            }
//        )
    }


    fun onBackClicked(lazyGridState: LazyGridState) {
        onHideNotification()
        val stateValue = state.value
        val visibleItemsInfo = lazyGridState.layoutInfo.visibleItemsInfo
        val imageIndex = stateValue.imageIndexesList[stateValue.pagerIndex]
        val gridItem = visibleItemsInfo.find { it.index == imageIndex }
        val isScrollToEnd: Boolean?
        var indexToScroll = imageIndex
        if (gridItem == null) {
            isScrollToEnd = imageIndex > visibleItemsInfo.last().index
        } else if (gridItem.offset.y < 0) {
            isScrollToEnd = false
            indexToScroll = gridItem.index
        } else if (gridItem.offset.y + gridItem.size.height > lazyGridState.layoutInfo.viewportSize.height) {
            isScrollToEnd = true
            indexToScroll = gridItem.index
        } else {
            isScrollToEnd = null
        }

        viewModelScope.launch {
            if(isScrollToEnd == null) {
                changeCurrentGridItemOffset(gridItem!!.offset)
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    onScrollToImage(indexToScroll, isScrollToEnd)
                }.join()
                val newGridItem = lazyGridState.layoutInfo.visibleItemsInfo.find { it.index ==  imageIndex } ?: visibleItemsInfo.last()
                changeCurrentGridItemOffset(newGridItem.offset)
            }
            _state.update {
                it.copy(
                    topBarVisible = false,
                    animationState = it.animationState.copy(
                        isAnimationInProgress = true,
                        animationType = AnimationType.HIDE_ANIMATION
                    )
                )
            }
            delay(350)
            _state.update {
                it.copy(
                    systemNavigationBarVisible = true,
                    topBarText = "",
                    isVisible = false,
                    animationState = it.animationState.copy(
                        isAnimationInProgress = true
                    )
                )
            }
        }
    }
}