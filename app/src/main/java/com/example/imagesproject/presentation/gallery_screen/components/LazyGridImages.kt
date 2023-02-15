package com.example.imagesproject.presentation.gallery_screen.components

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import kotlinx.collections.immutable.ImmutableList

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
        .height(100.dp)

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
        ,
        state = lazyGridState,
        columns = GridCells.Fixed(Constants.IMAGES_GRID_COLUMNS_COUNT),
    ) {
        items(imagesUrlList.size) { index ->
            val imageUrl = imagesUrlList[index]
            var isSuccess by rememberSaveable {
                mutableStateOf(true)
            }
            val contentScale = ContentScale.Crop
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .size(coil.size.Size.ORIGINAL)
                    .build(),
                filterQuality = FilterQuality.None,
                contentScale = contentScale
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
                            var delta = 0
                            val dsfasdf = convertPixelsToDp(74f, context)
                            val g = dsfasdf
                            if(size.height > size.width) {
                                delta = g- convertPixelsToDp(lastElement.size.toSize().height, context)
                            } else {
                                delta = convertPixelsToDp(size.height, context) - convertPixelsToDp(lastElement.size.toSize().height, context)
                            }
                            val d = delta
                            val lastColumn = Constants.IMAGES_GRID_COLUMNS_COUNT
                            val lastFullVisibleIndex =
                                lastElement.index - lastElement.column - 1
                            val firstFullVisibleIndex =
                                visibleItems.first().index + lastColumn - 1
                            val offset =
                                lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
                            val intSizeDp = convertPixelsSizeToDp(
                                context = context,
                                size = lastElement.size.toSize()
                            )
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemOffsetToScroll(offset))
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemSize(intSizeDp))
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveCurrentGridItemOffset(index))
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
//            AsyncImage(
//                model = imageUrl,
//                modifier = gridItemModifier
//                    .clickable(
//                        enabled = isSuccess,
//                        onClick = {
//                            val visibleItems =
//                                lazyGridState.layoutInfo.visibleItemsInfo
//                            val lastElement = visibleItems.last()
//                            val lastColumn = Constants.IMAGES_GRID_COLUMNS_COUNT
//                            val lastFullVisibleIndex =
//                                lastElement.index - lastElement.column - 1
//                            val firstFullVisibleIndex =
//                                visibleItems.first().index + lastColumn - 1
//                            val offset =
//                                lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
//                            val intSizeDp = convertPixelsToDp(
//                                context = context,
//                                size = lastElement.size.toSize()
//                            )
//                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemOffsetToScroll(offset))
//                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemSize(intSizeDp))
//                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveCurrentGridItemOffset(index))
//                            onGalleryScreenEvent(
//                                GalleryScreenEvent.OnSaveGridVisibleInterval(
//                                    startIndex = if (firstFullVisibleIndex < 0) lastColumn - 1 else firstFullVisibleIndex,
//                                    endIndex = if (lastFullVisibleIndex < 0) 0 else lastFullVisibleIndex,
//                                ))
//                            for(i in notValidImageIndexes) {
//                                onGalleryScreenEvent(GalleryScreenEvent.OnSaveNotValidImageIndex(i))
//                            }
//                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
//                        }
//                    ),
//                filterQuality = FilterQuality.None,
//                contentScale = CustomScale(),
//                error = painterResource(id = R.drawable.image_not_found),
//                placeholder = if(!isSuccess)
//                    painterResource(id = R.drawable.image_not_found)
//                else
//                    painterResource(id = R.drawable.placeholder),
//                onError = {
//                    notValidImageIndexes.add(index)
//                    isSuccess = false
//                },
//                contentDescription = null,
//            )
        }
    }
}

fun convertPixelsSizeToDp(size: Size, context: Context?): IntSize {
    val padding = 0
    val height = convertPixelsToDp(
        pixels = size.height,
        context = context,
    )
    val width = convertPixelsToDp(
        pixels = size.width,
        context = context,
    )
    return IntSize(width = width - padding, height = height - padding) // '-2' is for padding matching
}

fun convertPixelsToDp(pixels: Float, context: Context?): Int {
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    } else {
        val metrics = Resources.getSystem().displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}

fun convertDpToPixels(pixels: Float, context: Context?): Int {
    val dsf = 10.dp
    pixels
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    } else {
        val metrics = Resources.getSystem().displayMetrics
        (pixels / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}