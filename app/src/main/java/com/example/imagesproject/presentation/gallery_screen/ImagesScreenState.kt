package com.example.imagesproject.presentation.gallery_screen

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.example.imagesproject.core.util.UiText

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: UiText? = null,
    val topBarVisible: Boolean = false,
    val systemNavigationBarVisible: Boolean = true,
    val lazyGridState: LazyGridState = LazyGridState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
    val indexToScroll: Int? = null,
    val itemOffsetToScroll: Int = 0,
    val imageScreenState: ImageScreenState = ImageScreenState(),
)