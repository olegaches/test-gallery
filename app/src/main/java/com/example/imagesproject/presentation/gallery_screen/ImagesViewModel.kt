package com.example.imagesproject.presentation.gallery_screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.use_case.GetImagesUrlListUseCase
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.example.imagesproject.presentation.util.convertPixelsToDp
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

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getImagesUrlListUseCase: GetImagesUrlListUseCase,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _state = MutableStateFlow(ImagesScreenState())
    val state = _state.asStateFlow()
    var filteredData = savedStateHandle.saveable(
        key = "state",
        init = {
            mutableStateOf(_state.value)
        }
    )

    init {
        //savedStateHandle["imagesState"] = _state.value
//        Log.e("init", "init")
//        if(filteredData.value.imagesList.size != 0) {
//            Log.e("iamge saved", filteredData.value.imagesList[0])
//        }
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
                    val visibleItemsInfo = stateValue.lazyGridState.layoutInfo.visibleItemsInfo
                    val lastVisibleIndex = visibleItemsInfo.last().index
                    val firstVisibleIndex = visibleItemsInfo.first().index
                    if(event.index > lastVisibleIndex || event.index < firstVisibleIndex) {
                        onScrollToImage(event.index).join()
                    }
                    val currentElement = stateValue.lazyGridState.layoutInfo.visibleItemsInfo.find { it.index == event.index }
                        ?: stateValue.lazyGridState.layoutInfo.visibleItemsInfo.last()
                    val currentGridItemOffset = currentElement.offset
                    changeCurrentGridItemOffset(currentGridItemOffset)
                    changeImageSize(event.painterIntrinsicSize)
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

    private fun changeImageSize(size: Size) {
        _state.update {
            val imageState = _state.value.imageScreenState
            if(imageState.painterIntrinsicSize == size)
                return
            it.copy(
                imageScreenState = it.imageScreenState.copy(
                    painterIntrinsicSize = size
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
            stateValue.lazyGridState.scrollToItem(
                index = imageIndex,
                scrollOffset =
                if(stateValue.lazyGridState.layoutInfo.visibleItemsInfo.last().index <= imageIndex)
                    -stateValue.itemOffsetToScroll else {
                        0
                }
            )
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
                            gridItemSize = DpSize(convertPixelsToDp(gridItemSize.width, null).dp, convertPixelsToDp(gridItemSize.height, null).dp),
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
        val stateValue = state.value
        val imageStateValue = stateValue.imageScreenState
        val visibleItemsInfo = stateValue.lazyGridState.layoutInfo.visibleItemsInfo
        val imageIndex = imageStateValue.imageIndexesList[imageStateValue.pagerIndex]
        if(imageIndex > visibleItemsInfo.last().index || imageIndex < visibleItemsInfo.first().index) {
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
            delay(350) // 250
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