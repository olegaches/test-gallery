package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.gallery_screen.convertPixelsToDp
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LazyGridImages(
    lazyGridState: LazyGridState,
    imagesUrlList: ImmutableList<String>,
    onGalleryScreenEvent: (GalleryScreenEvent) -> Unit,
) {
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
            var isSuccess by remember {
                mutableStateOf(true)
            }
            LaunchedEffect(key1 = isSuccess) {
                if(!isSuccess) {
                    onGalleryScreenEvent(GalleryScreenEvent.OnSaveNotValidImageIndex(index))
                }
            }
            AsyncImage(
                model = imageUrl,
                modifier = gridItemModifier
                    .clickable(
                        enabled = isSuccess,
                        onClick = {
                            val visibleItems =
                                lazyGridState.layoutInfo.visibleItemsInfo
                            val lastElement = visibleItems.last()
                            val lastColumn = Constants.IMAGES_GRID_COLUMNS_COUNT
                            val lastFullVisibleIndex =
                                lastElement.index - lastElement.column - 1
                            val firstFullVisibleIndex =
                                visibleItems.first().index + lastColumn - 1
                            val offset =
                                lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
                            val intSizeDp = convertPixelsToDp(
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
                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
                        }
                    ),
                filterQuality = FilterQuality.None,
                contentScale = ContentScale.FillWidth,
                error = painterResource(id = R.drawable.image_not_found),
                placeholder = painterResource(id = R.drawable.placeholder),
                onError = {
                    isSuccess = false
                },
                contentDescription = null,
            )
        }
    }
}