package com.example.imagesproject.presentation.gallery_screen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AnimationState(
    val animationType: AnimationType = AnimationType.HIDE_ANIMATION,
    val isAnimationInProgress: Boolean = true,
): Parcelable