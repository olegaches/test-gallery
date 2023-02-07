package com.example.imagesproject.domain.model

import androidx.compose.ui.geometry.Offset

data class ImageItem(
    val url: String,
    val offset: Offset? = null,
)