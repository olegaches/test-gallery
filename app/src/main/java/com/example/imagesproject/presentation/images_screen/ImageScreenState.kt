package com.example.imagesproject.presentation.images_screen

import androidx.compose.ui.unit.IntSize

data class ImageScreenState(
    val isAnimatedScale: Boolean = false,
    val animationState: AnimationState = AnimationState(),
    val imageIndex: Int = 0,
    val isVisible: Boolean = false,
    val gridItemImageSize: IntSize = IntSize.Zero,
)