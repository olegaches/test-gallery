package com.example.imagesproject.presentation.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.ui.geometry.Size
import kotlin.math.max

fun findCropScale(srcSize: Size, dstSize: Size): Float {
    val widthScale = computeFillWidth(srcSize, dstSize)
    val heightScale = computeFillHeight(srcSize, dstSize)
    return max(widthScale, heightScale)
}

fun computeFillWidth(srcSize: Size, dstSize: Size): Float =
    dstSize.width / srcSize.width

fun computeFillHeight(srcSize: Size, dstSize: Size): Float =
    dstSize.height / srcSize.height

fun findFinalHeight(imageWidth: Float, imageHeight: Float, context: Context?): Float  {
    val deltaHeight = convertPixelsToDp(imageHeight - imageWidth, context)
    val convertedGridHeight = convertPixelsToDp(imageWidth, context)
    return deltaHeight + convertedGridHeight
}

fun findFinalWidth(imageHeight: Float, imageWidth: Float, context: Context?): Float {
    return findFinalHeight(imageHeight, imageWidth, context)
}

fun convertPixelsToDp(
    pixels: Float,
    context: Context?
): Float {
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
    } else {
        val metrics = Resources.getSystem().displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
    }
}