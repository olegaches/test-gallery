package com.example.imagesproject.presentation.gallery_screen.images_list

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.imagesproject.R
import com.example.imagesproject.presentation.util.convertPixelsToDp
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LazyGridImages(
    lazyGridState: LazyGridState,
    imagesUrlList: ImmutableList<String>,
    onGalleryScreenEvent: (GalleryScreenEvent) -> Unit,
) {
    val localContext = LocalContext.current
//    val a = convertPixelsToDp(280f, localContext)
   // val b = a
    val adapter = remember { ImageListAdapter() }
    val layoutManager = remember { AdaptiveGridLayoutManager(localContext, 270) }
    LaunchedEffect(key1 = imagesUrlList) {
        adapter.submitList(imagesUrlList)
    }
    Scaffold {
        Box(modifier = Modifier
            .fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    RecyclerView(context).apply {
                        this.layoutManager = layoutManager
                        this.adapter = adapter
                        this.layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    }
                }
            )
        }
    }


//    val context = LocalContext.current
//    val gridItemModifier = Modifier
//        .padding(1.dp)
//        .fillMaxWidth()
//        .aspectRatio(1f)
//
//    LazyVerticalGrid(
//        modifier = Modifier
//            .fillMaxSize(),
//        state = lazyGridState,
//        columns = GridCells.Adaptive(90.dp),
//    ) {
//        itemsIndexed(
//            imagesUrlList
//        ) { index, imageUrl ->
//            var isSuccess by rememberSaveable {
//                mutableStateOf(true)
//            }
//            val imageNotFoundId = remember { R.drawable.image_not_found }
//            val contentScale = remember { ContentScale.Crop }
//            val painter = rememberAsyncImagePainter(
//                model = ImageRequest
//                    .Builder(context)
//                    .data(imageUrl)
//                    .size(coil.size.Size.ORIGINAL)
//                    .error(imageNotFoundId)
//                    .placeholder(
//                        if(isSuccess)
//                            R.drawable.placeholder else {
//                            imageNotFoundId
//                        }
//                    )
//                    .build(),
//                filterQuality = FilterQuality.None,
//                contentScale = contentScale,
//                onState = { imageState ->
//                    when(imageState) {
//                        is AsyncImagePainter.State.Error -> {
//                            isSuccess = false
//                        }
//                        is AsyncImagePainter.State.Success -> {
//                            isSuccess = true
//                        }
//                        else -> {}
//                    }
//                }
//            )
//            Image(
//                modifier = gridItemModifier
//                    .clickable(
//                        onClick = {
//                            val size = painter.intrinsicSize
//                            val layoutInfo = lazyGridState.layoutInfo
//                            val visibleItems =
//                                layoutInfo.visibleItemsInfo
//                            val lastElement = visibleItems.last()
//                            val currentGridItemOffset =
//                                visibleItems.find { it.index == index }?.offset ?: IntOffset.Zero
//                            val offset =
//                                layoutInfo.viewportSize.height - lastElement.size.height
//                            onGalleryScreenEvent(GalleryScreenEvent.OnSavePainterIntrinsicSize(size))
//                            onGalleryScreenEvent(
//                                GalleryScreenEvent.OnSaveGridItemOffsetToScroll(
//                                    offset
//                                )
//                            )
//                            onGalleryScreenEvent(
//                                GalleryScreenEvent.OnSaveCurrentGridItemOffset(
//                                    currentGridItemOffset
//                                )
//                            )
//                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
//                        }
//                    ),
//                painter = painter,
//                contentScale = contentScale,
//                contentDescription = null,
//            )
//        }
//    }
}