package com.example.imagesproject.presentation.gallery_screen.components

import android.content.Context
import com.example.imagesproject.R
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.max

@Composable
fun LazyGridImages(
    lazyGridState: LazyGridState,
    imagesUrlList: ImmutableList<String>,
    onGalleryScreenEvent: (GalleryScreenEvent) -> Unit,
) {
    val notValidImageIndexes = mutableListOf<Int>()
    val context = LocalContext.current
    val gridItemModifier = Modifier
        .padding(1.dp)
        .fillMaxWidth()
        .aspectRatio(1f)

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
        ,
        state = lazyGridState,
        columns = GridCells.Adaptive(100.dp)
    ) {
        items(imagesUrlList.size) { index ->
            val imageUrl = imagesUrlList[index]
            var isSuccess by rememberSaveable {
                mutableStateOf(true)
            }
            val contentScale = ContentScale.Crop
            val painter = rememberAsyncImagePainter(
                model = ImageRequest
                    .Builder(context)
                    .data(imageUrl)
                    .size(coil.size.Size.ORIGINAL)
                    .error(R.drawable.image_not_found)
                    .placeholder(
                        if(isSuccess)
                            R.drawable.placeholder else {
                            R.drawable.image_not_found
                        }
                    )
                    .build(),
                filterQuality = FilterQuality.None,
                contentScale = contentScale,
                onState = { imageState ->
                    when(imageState) {
                        is AsyncImagePainter.State.Error -> {
                            notValidImageIndexes.add(index)
                            isSuccess = false
                        }
                        is AsyncImagePainter.State.Success -> {
                            isSuccess = true
                        }
                        else -> {}
                    }
                }
            )
            Image(
                modifier = gridItemModifier
                    .clickable(
                        enabled = isSuccess,
                        onClick = {
                            val size = painter.intrinsicSize
                            val visibleItems =
                                lazyGridState.layoutInfo.visibleItemsInfo
                            val lastElement = visibleItems.last()
                            val cropScale = findCropScale(size, lastElement.size.toSize())
                            val imageHeight = size.height * cropScale
                            val imageWidth = size.width * cropScale
                            val currentGridItemOffset = visibleItems.find { it.index == index }?.offset ?: IntOffset.Zero
                            if(imageHeight > imageWidth) {
                                val height = findFinalHeight(imageWidth, imageHeight, context)
                                val width = convertPixelsToDp(lastElement.size.toSize().width, context)
                                val itemOffset = currentGridItemOffset.copy(
                                    y = currentGridItemOffset.y - (imageHeight - imageWidth).toInt() / 2
                                )
                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveCurrentGridItemOffset(itemOffset))
                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemSize(DpSize(height = height.dp, width = width.dp)))
                            } else {
                                val width = findFinalWidth(imageHeight, imageWidth, context)
                                val height = convertPixelsToDp(lastElement.size.toSize().height, context)
                                val itemOffset = currentGridItemOffset.copy(
                                    x = currentGridItemOffset.x - (imageWidth - imageHeight).toInt() / 2
                                )
                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveCurrentGridItemOffset(itemOffset))
                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemSize(DpSize(height = height.dp, width = width.dp)))
                            }
                            val lastColumn = lastElement.column + 1 // Constants.IMAGES_GRID_COLUMNS_COUNT тут баг, если последний элемент в первой колонке.
                            // По другому пока никак, нам неизвестно кол-во колонок или столбцов.
                            val lastFullVisibleIndex =
                                lastElement.index - lastElement.column - 1
                            val firstFullVisibleIndex =
                                visibleItems.first().index + lastColumn - 1
                            val offset =
                                lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemOffsetToScroll(offset))
                            onGalleryScreenEvent(
                                GalleryScreenEvent.OnSaveGridVisibleInterval(
                                    startIndex = if (firstFullVisibleIndex < 0) lastColumn - 1 else firstFullVisibleIndex,
                                    endIndex = if (lastFullVisibleIndex < 0) 0 else lastFullVisibleIndex,
                                ))
                            for(i in notValidImageIndexes) {
                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveNotValidImageIndex(i))
                            }
                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
                        }
                    ),
                painter = painter,
                contentScale = contentScale,
                contentDescription = null,
            )
        }
    }
}

private fun findCropScale(srcSize: Size, dstSize: Size): Float {
    val widthScale = computeFillWidth(srcSize, dstSize)
    val heightScale = computeFillHeight(srcSize, dstSize)
    return max(widthScale, heightScale)
}

private fun computeFillWidth(srcSize: Size, dstSize: Size): Float =
    dstSize.width / srcSize.width

private fun computeFillHeight(srcSize: Size, dstSize: Size): Float =
    dstSize.height / srcSize.height

fun findFinalHeight(imageWidth: Float, imageHeight: Float, context: Context?): Float  {
    val deltaHeight = convertPixelsToDp(imageHeight - imageWidth, context)
    val convertedGridHeight = convertPixelsToDp(imageWidth, context)
    return deltaHeight + convertedGridHeight
}

fun findFinalWidth(imageHeight: Float, imageWidth: Float, context: Context?): Float {
    return findFinalHeight(imageHeight, imageWidth, context)
}

fun convertPixelsToDp(pixels: Float, context: Context?): Float {
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
    } else {
        val metrics = Resources.getSystem().displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
    }
}