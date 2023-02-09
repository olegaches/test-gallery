package com.example.imagesproject.presentation.images_screen

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.unit.IntSize
import com.example.imagesproject.core.util.UiText
import com.example.imagesproject.domain.model.ImageItem

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<ImageItem> = emptyList(),
    val error: UiText? = null,
    val topBarVisible: Boolean = false,
    val systemNavigationBarVisible: Boolean = true,
    val openedImageLayer: Boolean = false,
    val lazyGridState: LazyGridState = LazyGridState(),
    val gridLayoutParams: GridLayoutParams? = null,
    val indexToScroll: Int? = null,
    val imageScreenState: ImageScreenState = ImageScreenState(),
    val gridItemSize: IntSize = IntSize.Zero,
)