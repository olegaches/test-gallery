package com.example.imagesproject.presentation.gallery_screen.ui_events

import androidx.compose.ui.geometry.Size
import com.example.imagesproject.presentation.gallery_screen.AnimationType
sealed interface ImageScreenEvent {
    data class OnAnimate(val value: AnimationType): ImageScreenEvent
    data class OnVisibleChanged(val value: Boolean): ImageScreenEvent
    data class OnPagerIndexChanged(val value: Int): ImageScreenEvent
    data class OnPagerCurrentImageChange(val index: Int, val painterIntrinsicSize: Size): ImageScreenEvent
    data class OnTopBarTitleTextChange(val titleText: String): ImageScreenEvent
    object OnBackToGallery: ImageScreenEvent
    object OnBarsVisibilityChange: ImageScreenEvent
}