package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImage
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.ParcelableSize
import com.skydoves.orbital.OrbitalScope
import com.skydoves.orbital.animateSharedElementTransition

@Composable
fun AnimatedImageBox(
    isRightLayoutDirection: Boolean,
    paddingValues: PaddingValues,
    painterIntrinsicSize: ParcelableSize,
    animationType: AnimationType,
    isHorizontalOrientation: Boolean,
    currentScale: Float,
    offset: IntOffset,
    gridItemSize: Dp,
    orbitalScope: OrbitalScope,
    imageUrl: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val ratio = painterIntrinsicSize.width/painterIntrinsicSize.height
        AsyncImage(
            modifier = if (animationType == AnimationType.EXPAND_ANIMATION) {
                Modifier
                    .fillMaxSize(currentScale)
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
                    .offset { offset }
                    .size(
                        gridItemSize
                    )
            }.animateSharedElementTransition(
                orbitalScope,
                SpringSpec(stiffness = 1200f),
                SpringSpec(stiffness = 1200f)
            ),
            model = imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}