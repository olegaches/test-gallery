package com.example.imagesproject.presentation.gallery_screen

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageScreenState(
    val isAnimatedScale: Boolean = false,
    val animationState: AnimationState = AnimationState(),
    val pagerIndex: Int = 0,
    val isVisible: Boolean = false,
    val gridItemSize: ParcelableSize = ParcelableSize.Zero,
    val imageOffset: ParcelableIntOffset = ParcelableIntOffset.Zero,
    val painterIntrinsicSize: ParcelableSize = ParcelableSize.Zero,
    val imageIndexesList: List<Int> = emptyList(),
): Parcelable

@Parcelize
data class ParcelableSize(
    val width: Float,
    val height: Float,
): Parcelable {
    fun toSize(): Size {
        return Size(width, height)
    }
    companion object {
        @Stable
        val Zero = ParcelableSize(0.0f, 0.0f)
        @Stable
        val Unspecified = ParcelableSize(Float.NaN, Float.NaN)
    }
}

@Parcelize
data class ParcelableIntOffset(
    val x: Int,
    val y: Int,
): Parcelable {

    fun toIntOffset(): IntOffset {
        return IntOffset(x ,y)
    }
    companion object {
        val Zero = ParcelableIntOffset(0, 0)
    }
}