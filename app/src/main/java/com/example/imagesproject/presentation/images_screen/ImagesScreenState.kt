package com.example.imagesproject.presentation.images_screen

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.example.imagesproject.core.util.UiText

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: UiText? = null,
    val topBarVisible: Boolean = false,
    val systemNavigationBarVisible: Boolean = true,
    val openedImageLayer: Boolean = false,
    val lazyGridState: LazyGridState = LazyGridState(),
    val gridLayoutParams: GridLayoutParams? = null,
    val indexToScroll: Int? = null,
    val imageScreenState: ImageScreenState = ImageScreenState(),
)