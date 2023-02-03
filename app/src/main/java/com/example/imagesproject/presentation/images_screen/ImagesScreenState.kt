package com.example.imagesproject.presentation.images_screen

import com.example.imagesproject.core.util.UiText

data class ImagesScreenState(
    val isLoading: Boolean = false,
    val imagesList: List<String> = emptyList(),
    val error: UiText? = null,
    val isExpanded: Boolean = false,
)