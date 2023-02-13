package com.example.imagesproject.presentation.gallery_screen.ui_events

import androidx.compose.ui.unit.IntSize

sealed interface GalleryScreenEvent {
    data class OnSaveGridItemSize(val intSize: IntSize): GalleryScreenEvent
    data class OnSaveGridItemOffsetToScroll(val yOffset: Int): GalleryScreenEvent
    data class OnSaveCurrentGridItemOffset(val index: Int): GalleryScreenEvent
    data class OnSaveNotValidImageIndex(val index: Int): GalleryScreenEvent
    data class OnSaveGridVisibleInterval(val startIndex: Int, val endIndex: Int): GalleryScreenEvent
    data class OnImageClick(val index: Int): GalleryScreenEvent
}