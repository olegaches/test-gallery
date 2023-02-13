package com.example.imagesproject.presentation.gallery_screen

import androidx.compose.foundation.lazy.grid.LazyGridState
import com.example.imagesproject.core.util.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: ImmutableList<String> = persistentListOf(),
    val error: UiText? = null,
    val topBarVisible: Boolean = false,
    val topBarTitleText: String = "",
    val systemNavigationBarVisible: Boolean = true,
    val lazyGridState: LazyGridState = LazyGridState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
    val indexToScroll: Int? = null,
    val itemOffsetToScroll: Int = 0,
    val imageScreenState: ImageScreenState = ImageScreenState(),
    val notValidImagesIndexes: ImmutableList<Int> = persistentListOf(),
)