package com.example.imagesproject.presentation.gallery_screen

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ImageScreenState(
    val isAnimatedScale: Boolean = false,
    val animationState: AnimationState = AnimationState(),
    val pagerIndex: Int = 0,
    val isVisible: Boolean = false,
    val gridItemSize: DpSize = DpSize.Zero,
    val imageOffset: IntOffset = IntOffset.Zero,
    val painterIntrinsicSize: Size = Size.Zero,
    val imageIndexesList: ImmutableList<Int> = persistentListOf(),
)