package com.example.imagesproject.presentation.gallery_screen.images_list

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset

@Stable
sealed interface GalleryScreenEvent {
    data class OnSavePainterIntrinsicSize(val size: Size): GalleryScreenEvent
    data class OnSaveGridItemOffsetToScroll(val yOffset: Int): GalleryScreenEvent
    data class OnSaveCurrentGridItemOffset(val intOffset: IntOffset): GalleryScreenEvent
    data class OnSaveNotValidImageIndex(val index: Int): GalleryScreenEvent
    data class OnImageClick(val index: Int): GalleryScreenEvent
}