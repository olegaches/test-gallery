package com.example.imagesproject.presentation.gallery_screen.ui_events

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import com.example.imagesproject.presentation.gallery_screen.AnimationType
sealed interface ImageScreenEvent {
    data class OnAnimate(val value: AnimationType): ImageScreenEvent
    data class OnVisibleChanged(val value: Boolean): ImageScreenEvent
    data class OnPagerIndexChanged(val value: Int): ImageScreenEvent
    data class OnGridItemOffsetChange(val index: Int, val imageSize: Size): ImageScreenEvent
    data class OnGridItemSizeChange(val value: DpSize): ImageScreenEvent
    data class OnTopBarTitleTextChange(val titleText: String): ImageScreenEvent
    object OnBackToGallery: ImageScreenEvent
    object OnBarsVisibilityChange: ImageScreenEvent
}