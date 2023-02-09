package com.example.imagesproject.presentation.images_screen

sealed interface ImageScreenEvent {
    data class IsAnimatedScaleChanged(val value: Boolean): ImageScreenEvent
}