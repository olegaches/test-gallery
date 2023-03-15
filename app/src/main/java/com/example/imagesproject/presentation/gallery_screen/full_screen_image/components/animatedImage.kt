package com.example.imagesproject.presentation.gallery_screen.full_screen_image.components

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.PagerScreenState
import com.skydoves.orbital.OrbitalScope
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope

@Composable
fun animatedImage(
    imageUrl: String,
    animationType: AnimationType,
    pagerScreenState: PagerScreenState,
    paddingValues: PaddingValues,
    isHorizontalOrientation: Boolean,
    isRightLayoutDirection: Boolean,
) : @Composable() (OrbitalScope.() -> Unit){
    return rememberContentWithOrbitalScope {
        val orbitalScope = this
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val ratio = pagerScreenState.painterIntrinsicSize.width/pagerScreenState.painterIntrinsicSize.height
            var isSuccess by remember {
                mutableStateOf(true)
            }
            AsyncImage(
                modifier = if (animationType == AnimationType.EXPAND_ANIMATION) {
                    Modifier
                        .fillMaxSize(pagerScreenState.currentScale)
                        .aspectRatio(ratio, isHorizontalOrientation)
                        .align(Alignment.Center)
                } else {
                    Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateLeftPadding(
                                if (isRightLayoutDirection)
                                    LayoutDirection.Rtl else
                                    LayoutDirection.Ltr
                            ),
                        )
                        .offset { pagerScreenState.imageOffset.toIntOffset() }
                        .size(
                            pagerScreenState.gridItemSize.width.dp,
                            pagerScreenState.gridItemSize.width.dp
                        )
                }.animateSharedElementTransition(
                    orbitalScope,
                    SpringSpec(stiffness = 1200f),
                    SpringSpec(stiffness = 1200f)
                ),
                onError = {
                    isSuccess = false
                },
                placeholder = painterResource(id = R.drawable.image_not_found),
                error = painterResource(id = R.drawable.image_not_found),
                model = imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
    }
}