package com.example.imagesproject.presentation.images_screen

data class AnimationState(
    val animationType: AnimationType = AnimationType.HIDE_ANIMATION,
    val isAnimationInProgress: Boolean = true,
)