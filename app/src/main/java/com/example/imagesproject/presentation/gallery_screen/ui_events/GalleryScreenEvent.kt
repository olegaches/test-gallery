package com.example.imagesproject.presentation.gallery_screen.ui_events

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset

@Stable
sealed interface GalleryScreenEvent {
    data class OnSaveGridItemSize(val dpSize: DpSize): GalleryScreenEvent
    data class OnSaveGridItemOffsetToScroll(val yOffset: Int): GalleryScreenEvent
    data class OnSaveCurrentGridItemOffset(val intOffset: IntOffset): GalleryScreenEvent
    data class OnSaveNotValidImageIndex(val index: Int): GalleryScreenEvent
    data class OnSaveGridVisibleInterval(val startIndex: Int, val endIndex: Int): GalleryScreenEvent
    data class OnImageClick(val index: Int): GalleryScreenEvent
}