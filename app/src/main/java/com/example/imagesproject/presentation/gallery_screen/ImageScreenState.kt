package com.example.imagesproject.presentation.gallery_screen

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

data class ImageScreenState(
    val isAnimatedScale: Boolean = false,
    val animationState: AnimationState = AnimationState(),
    val imageIndex: Int = 0,
    val isVisible: Boolean = false,
    val gridItemImageSize: IntSize = IntSize.Zero,
    val gridItemOffset: IntOffset = IntOffset.Zero,
    val visibleGridInterval: Pair<Int, Int> = Pair(0, 0),
)