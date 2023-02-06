package com.example.imagesproject.presentation.images_screen

import androidx.compose.ui.geometry.Offset
import com.example.imagesproject.core.util.UiText

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: UiText? = null,
    val isExpanded: Boolean = false,
    val currentImageIndex: Int = 0,
    val currentImageUrl: String? = null,
    val clickedImageGlobalOffset: Offset? = null,
    val topBarVisible: Boolean = false,
    val systemNavigationBarVisible: Boolean = true,
)