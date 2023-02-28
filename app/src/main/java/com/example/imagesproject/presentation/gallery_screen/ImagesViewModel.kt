package com.example.imagesproject.presentation.gallery_screen

import android.app.Notification
import android.app.NotificationManager
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetAppConfigurationStreamUseCase
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.example.imagesproject.presentation.util.convertPixelsToDp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getImagesUrlListUseCase: GetImagesUrlListUseCase,
    private val notificationManager: NotificationManager,
    private val savedStateHandle: SavedStateHandle,
    private val getAppConfigurationStreamUseCase: GetAppConfigurationStreamUseCase,
): ViewModel() {

    private val _state by savedStateHandle.saveable(saver = ImagesScreenState.Saver, init = { MutableStateFlow(ImagesScreenState()) })
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
                changeImageSize(event.painterIntrinsicSize)
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
            is ImageScreenEvent.OnShowNotification -> {
                notifyOpenedImage(event.notification)
            }
            is ImageScreenEvent.OnHideNotification -> {
                onHideNotification()
            }
        }
    }

    private fun onHideNotification() {
        notificationManager.cancel(1)
    }

    private fun changeImageSize(size: Size) {
        _state.update {
            val imageState = _state.value.imageScreenState
            if(imageState.painterIntrinsicSize.toSize() == size)
                return
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    painterIntrinsicSize = ParcelableSize(size.width, size.height)
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

    private suspend fun onScrollToImage(imageIndex: Int, isScrollToEnd: Boolean) {
        val stateValue = _state.value
        stateValue.lazyGridState.scrollToItem(
            index = imageIndex,
            scrollOffset =
            if(isScrollToEnd)
                -stateValue.itemOffsetToScroll else {
                0
            }
        )
    }

    private fun changeCurrentGridItemOffset(intOffset: IntOffset) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    imageScreenState = it.imageScreenState.copy(
                        imageOffset = ParcelableIntOffset(intOffset.x, intOffset.y)
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
            is GalleryScreenEvent.OnSavePainterIntrinsicSize -> {
                changeImageSize(event.size)
            }
            is GalleryScreenEvent.OnSaveCurrentGridItemOffset -> {
                changeCurrentGridItemOffset(event.intOffset)
            }
            is GalleryScreenEvent.OnImageClick -> {
                val newList = mutableListOf<Int>()
                val stateValue = _state.value
                val gridItemSize = stateValue.lazyGridState.layoutInfo.visibleItemsInfo.first().size.toSize()
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
                            gridItemSize = ParcelableSize(convertPixelsToDp(gridItemSize.width, null), convertPixelsToDp(gridItemSize.height, null)),
                            imageIndexesList = newList
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
        onHideNotification()
        val stateValue = state.value
        val imageStateValue = stateValue.imageScreenState
        val visibleItemsInfo = stateValue.lazyGridState.layoutInfo.visibleItemsInfo
        val imageIndex = imageStateValue.imageIndexesList[imageStateValue.pagerIndex]
        val gridItem = visibleItemsInfo.find { it.index == imageIndex }
        val isScrollToEnd: Boolean?
        var indexToScroll = imageIndex
        if (gridItem == null) {
            isScrollToEnd = imageIndex > visibleItemsInfo.last().index
        } else if (gridItem.offset.y < 0) {
            isScrollToEnd = false
            indexToScroll = gridItem.index
        } else if (gridItem.offset.y + gridItem.size.height > stateValue.lazyGridState.layoutInfo.viewportSize.height) {
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
                val newGridItem = state.value.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index ==  imageIndex } ?: visibleItemsInfo.last()
                changeCurrentGridItemOffset(newGridItem.offset)
            }
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
            delay(350)
            _state.update {
                it.copy(
                    systemNavigationBarVisible = true,
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

    private fun notifyOpenedImage(notification: Notification) {
        notificationManager.notify(
            1,
            notification
        )
    }

    private fun loadImageUrlList() {
        viewModelScope.launch {
            getAppConfigurationStreamUseCase().collectLatest { appConfiguration ->
                _state.update {
                    it.copy(
                        currentTheme = appConfiguration.themeStyle,
                    )
                }
            }
        }
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