package com.example.imagesproject.presentation.images_screen

sealed interface ImageScreenEvent {
    data class IsAnimatedScaleChanged(val value: Boolean): ImageScreenEvent
    data class OnAnimate(val value: AnimationType): ImageScreenEvent
    data class OnVisibleChanged(val value: Boolean): ImageScreenEvent
}