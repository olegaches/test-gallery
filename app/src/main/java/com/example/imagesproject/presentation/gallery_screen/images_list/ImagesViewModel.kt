package com.example.imagesproject.presentation.gallery_screen.images_list

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.service.ImageService
import com.example.imagesproject.domain.use_case.DeleteImageUrlFromRoomDbUseCase
import com.example.imagesproject.domain.use_case.GetAppConfigurationStreamUseCase
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.GalleryScreenState
import com.example.imagesproject.presentation.gallery_screen.ParcelableIntOffset
import com.example.imagesproject.presentation.gallery_screen.ParcelableSize
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.ImageScreenEvent
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
    savedStateHandle: SavedStateHandle,
    private val getAppConfigurationStreamUseCase: GetAppConfigurationStreamUseCase,
    private val imageService: ImageService,
    private val deleteImageUrlFromRoomDbUseCase: DeleteImageUrlFromRoomDbUseCase,
): ViewModel() {

    private val _state by savedStateHandle.saveable(saver = GalleryScreenState.Saver, init = { MutableStateFlow(
        GalleryScreenState()
    ) })
    val state = _state.asStateFlow()

    init {
        init()
    }

    fun onImageScreenEvent(event: ImageScreenEvent) {
        when(event) {
            is ImageScreenEvent.OnAnimate -> {
                onAnimate(event.value)
            }
            is ImageScreenEvent.OnVisibleChanged -> {
                onImageVisibilityChange(event.value)
            }
            is ImageScreenEvent.OnPagerIndexChanged -> {
                onPagerIndexChanged(event.value)
            }
            is ImageScreenEvent.OnPagerCurrentImageChange -> {
                changeImageSize(event.painterIntrinsicSize)
            }
            is ImageScreenEvent.OnBarsVisibilityChange -> {
                onBarsVisibilityChange()
            }
            is ImageScreenEvent.OnBackToGallery -> {
                onBackClicked()
            }
            is ImageScreenEvent.OnHideNotification -> {
                onHideNotification()
            }
            is ImageScreenEvent.OnDeleteImageUrl -> {
                deleteImageUrl(event.pagerIndex)
            }
            is ImageScreenEvent.OnDeleteDialogVisibilityChange -> {
                changeDeleteDialogVisibility(event.visible)
            }
            is ImageScreenEvent.OnCurrentScaleChange -> {
                changeCurrentScale(event.scale)
            }
        }
    }

    private fun changeCurrentScale(scale: Float) {
        _state.update {
            it.copy(
                pagerScreenState = it.pagerScreenState.copy(
                    currentScale = scale
                )
            )
        }
    }

    private fun changeDeleteDialogVisibility(visible: Boolean) {
        _state.update {
            it.copy(
                pagerScreenState = it.pagerScreenState.copy(
                    deleteDialogOpened = visible
                )
            )
        }
    }

    private fun onImageVisibilityChange(isVisible: Boolean) {
        _state.update {
            it.copy(
                pagerScreenState = it.pagerScreenState.copy(
                    isVisible = isVisible
                )
            )
        }
    }

    private fun onAnimate(animationType: AnimationType) {
        viewModelScope.launch {
            val _state = _state
            _state.update {
                val pagerScreenState = it.pagerScreenState
                it.copy(
                    pagerScreenState = pagerScreenState.copy(
                        animationState = pagerScreenState.animationState.copy(
                            isAnimationInProgress = true,
                            animationType = animationType
                        )
                    )
                )
            }
            delay(400)
            _state.update {
                val pagerScreenState = it.pagerScreenState
                it.copy(
                    pagerScreenState = pagerScreenState.copy(
                        animationState = pagerScreenState.animationState.copy(
                            isAnimationInProgress = false,
                        )
                    )
                )
            }
        }
    }

    private fun onPagerIndexChanged(index: Int) {
        _state.update {
            val url = it.imagesList[index]
            notifyOpenedImage(url)
            it.copy(
                pagerScreenState = it.pagerScreenState.copy(
                    pagerIndex = index,
                    topBarText = url
                )
            )
        }
    }

    private fun deleteImageUrl(pagerIndex: Int) {
        viewModelScope.launch {
            val stateValue = state.value
            val imagesList = stateValue.imagesList
            val imageUrl = imagesList[pagerIndex]
            val newList = imagesList.minus(imageUrl)
            val newPagerIndex = if(newList.size == pagerIndex) {
                pagerIndex - 1
            } else {
                pagerIndex
            }
            deleteImageUrlFromRoomDbUseCase(imageUrl)
            _state.update {
                it.copy(
                    imagesList = newList,
                    pagerScreenState = it.pagerScreenState.copy(
                        pagerIndex = newPagerIndex
                    )
                )
            }
            if(newList.isEmpty()) {
                onHideNotification()
            } else  {
                onPagerIndexChanged(newPagerIndex)
            }
        }
    }

    private fun onHideNotification() {
        imageService.hideNotification()
    }

    private fun changeImageSize(size: Size) {
        _state.update {
            val imageState = it.pagerScreenState
            if(imageState.painterIntrinsicSize.toSize() == size)
                return
            it.copy(
                pagerScreenState = imageState.copy(
                    painterIntrinsicSize = ParcelableSize(size.width, size.height)
                )
            )
        }
    }

    private fun onBarsVisibilityChange() {
        _state.update {
            val pagerScreenState = it.pagerScreenState
            it.copy(
                pagerScreenState = pagerScreenState.copy(
                    topBarVisible = !pagerScreenState.topBarVisible,
                )
            )
        }
        onNavigationBarVisibilityChange()
    }

    private fun onNavigationBarVisibilityChange() {
        _state.update {
            val pagerScreenState = it.pagerScreenState
            it.copy(
                pagerScreenState = pagerScreenState
                    .copy(
                        systemNavigationBarVisible = pagerScreenState.topBarVisible
                    )
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
                    pagerScreenState = it.pagerScreenState.copy(
                        imageOffset = ParcelableIntOffset(intOffset.x, intOffset.y)
                    )
                )
            }
        }
    }

    fun onGalleryScreenEvent(event: GalleryScreenEvent) {
        when(event) {
            is GalleryScreenEvent.OnSaveGridItemOffsetToScroll -> {
                onItemOffsetToScrollChange(event.yOffset)
            }
            is GalleryScreenEvent.OnSavePainterIntrinsicSize -> {
                changeImageSize(event.size)
            }
            is GalleryScreenEvent.OnSaveCurrentGridItemOffset -> {
                changeCurrentGridItemOffset(event.intOffset)
            }
            is GalleryScreenEvent.OnImageClick -> {
                onImageClick(event.index)
            }
        }
    }

    private fun onItemOffsetToScrollChange(yOffset: Int) {
        _state.update {
            it.copy(
                itemOffsetToScroll = yOffset
            )
        }
    }

    private fun onImageClick(index: Int) {
        _state.update {
            val gridItemSize = it.lazyGridState.layoutInfo.visibleItemsInfo.first().size.toSize()
            val imageUrl = it.imagesList[index]
            notifyOpenedImage(imageUrl)
            it.copy(
                pagerScreenState = it.pagerScreenState.copy(
                    pagerIndex = index,
                    isVisible = true,
                    topBarText = imageUrl,
                    gridItemSize = ParcelableSize(convertPixelsToDp(gridItemSize.width, null), convertPixelsToDp(gridItemSize.height, null)),
                ),
            )
        }
    }

    private fun onBackClicked() {
        onHideNotification()
        val stateValue = state.value
        val imageStateValue = stateValue.pagerScreenState
        val layoutInfo = stateValue.lazyGridState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo
        val imageIndex = imageStateValue.pagerIndex
        val gridItem = visibleItemsInfo.find { it.index == imageIndex }
        val isScrollToEnd: Boolean?
        var indexToScroll = imageIndex
        val gridItemOffset = gridItem?.offset
        val gridItemOffsetY = gridItemOffset?.y
        if (gridItem == null) {
            isScrollToEnd = imageIndex > visibleItemsInfo.last().index
        } else if (gridItemOffsetY!! < 0) {
            isScrollToEnd = false
            indexToScroll = gridItem.index
        } else if (gridItemOffsetY + gridItem.size.height > layoutInfo.viewportSize.height) {
            isScrollToEnd = true
            indexToScroll = gridItem.index
        } else {
            isScrollToEnd = null
        }

        viewModelScope.launch {
            if(isScrollToEnd == null) {
                changeCurrentGridItemOffset(gridItemOffset!!)
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    onScrollToImage(indexToScroll, isScrollToEnd)
                }.join()
                val newGridItem = state.value.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index == imageIndex } ?: visibleItemsInfo.last()
                changeCurrentGridItemOffset(newGridItem.offset)
            }
            val _state = _state
            _state.update {
                val pagerScreenState = it.pagerScreenState
                it.copy(
                    pagerScreenState = pagerScreenState.copy(
                        topBarVisible = false,
                        animationState = pagerScreenState.animationState.copy(
                            isAnimationInProgress = true,
                            animationType = AnimationType.HIDE_ANIMATION
                        )
                    )
                )
            }
            delay(350)
            _state.update {
                val pagerScreenState = it.pagerScreenState
                it.copy(
                    pagerScreenState = pagerScreenState.copy(
                        systemNavigationBarVisible = true,
                        currentScale = 1f,
                        topBarText = "",
                        isVisible = false,
                        animationState = pagerScreenState.animationState.copy(
                            isAnimationInProgress = true
                        )
                    )
                )
            }
        }
    }

    private fun notifyOpenedImage(imageUrl: String) {
        imageService.showNotification(imageUrl)
    }

    fun onRefresh() {
        loadImageUrlList()
    }

    private fun loadImageUrlList() {
        viewModelScope.launch {
            val _state = _state
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

    private fun init() {
        viewModelScope.launch {
            getAppConfigurationStreamUseCase().collectLatest { appConfiguration ->
                _state.update {
                    it.copy(
                        pagerScreenState = it.pagerScreenState.copy(
                            currentTheme = appConfiguration.themeStyle,
                        )
                    )
                }
            }
        }
        loadImageUrlList()
    }
}