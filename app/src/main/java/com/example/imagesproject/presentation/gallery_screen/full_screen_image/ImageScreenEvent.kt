package com.example.imagesproject.presentation.gallery_screen.full_screen_image

import androidx.compose.ui.geometry.Size
import com.example.imagesproject.presentation.gallery_screen.AnimationType
sealed interface ImageScreenEvent {
    data class OnAnimate(val value: AnimationType): ImageScreenEvent
    data class OnDeleteImageUrl(val pagerIndex: Int): ImageScreenEvent
    data class OnDeleteDialogVisibilityChange(val visible: Boolean): ImageScreenEvent
    data class OnCurrentScaleChange(val scale: Float): ImageScreenEvent
    data class OnVisibleChanged(val value: Boolean): ImageScreenEvent
    data class OnPagerIndexChanged(val value: Int): ImageScreenEvent
    data class OnPagerCurrentImageChange(val painterIntrinsicSize: Size): ImageScreenEvent
    object OnHideNotification: ImageScreenEvent
    object OnBackToGallery: ImageScreenEvent
    object OnBarsVisibilityChange: ImageScreenEvent
}