package com.example.imagesproject.presentation.gallery_screen.images_list

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.imagesproject.R


class ImageViewHolder(
    //val composeView: ComposeView
    val composeView: View
) : RecyclerView.ViewHolder(composeView) {

    @OptIn(ExperimentalGlideComposeApi::class)
    fun bind(url: String) {
        with(composeView) {
            val imageView = this.findViewById<ImageView>(R.id.imageView)
            Glide
                .with(this.rootView.context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.image_not_found)
                .into(imageView)
            imageView.setOnClickListener {

            }
        }

//        composeView.setContent {
//            GlideImage(
//                modifier = Modifier
//                    .padding(1.dp)
//                    .fillMaxWidth()
//                    .aspectRatio(1f)
//                ,
//                model = url,
//                contentDescription = null,
//                requestBuilderTransform = { builder ->
//                    builder.placeholder(R.drawable.placeholder)
//                },
//                contentScale = ContentScale.Crop,
//            )
//        }

//        composeView.setContent {
//            val imageUrl = remember { url }
//            var isSuccess by rememberSaveable {
//                mutableStateOf(true)
//            }
//            val context = LocalContext.current
//            val imageNotFoundId = remember { R.drawable.image_not_found }
//            val contentScale = remember { ContentScale.Crop }
//            val painter = rememberAsyncImagePainter(
//                model = ImageRequest
//                    .Builder(context)
//                    .data(imageUrl)
//                    .size(Size.ORIGINAL)
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
//                modifier = Modifier
//                    .padding(1.dp)
//                    .fillMaxWidth()
//                    .aspectRatio(1f)
//                    .clickable(
//                        onClick = {
////                            val size = painter.intrinsicSize
////                            val layoutInfo = lazyGridState.layoutInfo
////                            val visibleItems =
////                                layoutInfo.visibleItemsInfo
////                            val lastElement = visibleItems.last()
////                            val currentGridItemOffset =
////                                visibleItems.find { it.index == index }?.offset ?: IntOffset.Zero
////                            val offset =
////                                layoutInfo.viewportSize.height - lastElement.size.height
////                            onGalleryScreenEvent(GalleryScreenEvent.OnSavePainterIntrinsicSize(size))
////                            onGalleryScreenEvent(
////                                GalleryScreenEvent.OnSaveGridItemOffsetToScroll(
////                                    offset
////                                )
////                            )
////                            onGalleryScreenEvent(
////                                GalleryScreenEvent.OnSaveCurrentGridItemOffset(
////                                    currentGridItemOffset
////                                )
////                            )
////                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
//                        }
//                    ),
//                painter = painter,
//                contentScale = contentScale,
//                contentDescription = null,
//            )
//        }
    }
}