package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.ImageScreenState
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagesList: ImmutableList<String>,
    imageScreenState: ImageScreenState,
    paddingValues: PaddingValues,
    onImageScreenEvent: (ImageScreenEvent) -> Unit,
) {
    LaunchedEffect(key1 = true) {
        if(imageScreenState.animationState.animationType != AnimationType.EXPAND_ANIMATION) {
            onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
            onImageScreenEvent(ImageScreenEvent.OnAnimate(AnimationType.EXPAND_ANIMATION))
        }
    }

    val imageIndexesList = imageScreenState.imageIndexesList

    var animationType by remember {
        mutableStateOf(imageScreenState.animationState.animationType)
    }
    LaunchedEffect(key1 = imageScreenState.animationState.animationType) {
        animationType = imageScreenState.animationState.animationType
    }

    val pagerState = rememberPagerState(
        initialPage = imageScreenState.pagerIndex
    )

    LaunchedEffect(key1 = pagerState.currentPage) {
        val index = imageIndexesList[pagerState.currentPage]
        onImageScreenEvent(ImageScreenEvent.OnTopBarTitleTextChange(imagesList[index]))
        onImageScreenEvent(ImageScreenEvent.OnGridItemOffsetChange(index))
    }
    val imageContent = rememberContentWithOrbitalScope {
        AsyncImage(
            modifier = if (animationType == AnimationType.EXPAND_ANIMATION) {
                Modifier
                    .fillMaxSize()
            } else {
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding()
                    )
                    .offset {
                        imageScreenState.gridItemOffset
                    }
                    .size(
                        height = imageScreenState.gridItemImageSize.height.dp,
                        width = imageScreenState.gridItemImageSize.width.dp,
                    )
            }.animateSharedElementTransition(
                this,
                SpringSpec(stiffness = 600f),
                SpringSpec(stiffness = 600f)
            ),
            model = imagesList[imageIndexesList[imageScreenState.pagerIndex]]
            ,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            error = painterResource(id = R.drawable.image_not_found),
        )
    }
    val backGroundColor by animateColorAsState(
        targetValue = if (animationType == AnimationType.EXPAND_ANIMATION) Color.Black else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )
    Box(
        modifier = Modifier
            .background(backGroundColor)
            .fillMaxSize()
    ) {
        if(imageScreenState.animationState.isAnimationInProgress) {
            Orbital(
                modifier = Modifier.fillMaxSize()
            ) {
                imageContent()
            }
        } else {
            LaunchedEffect(key1 = pagerState.currentPage) {
                onImageScreenEvent(ImageScreenEvent.OnPagerIndexChanged(
                    pagerState.currentPage
                ))
            }
            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 3,
                pageCount = imageIndexesList.size,
                modifier = Modifier
                    .fillMaxSize()
                ,
                pageSpacing = 16.dp,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(0)
                ),
                contentPadding = PaddingValues(0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { index ->
                val imageIndex = imageIndexesList[index]
                val f = OverZoomConfig(
                    minSnapScale = 1f,
                    maxSnapScale = 1.7f
                )
                val zoomableState = rememberZoomableState(
                    initialScale = 1f, // можно юзать для анимации?
                    minScale = 0.1f,
                    overZoomConfig = f,
                )
                LaunchedEffect(key1 = pagerState.targetPage) {
                    zoomableState.animateScaleTo(targetScale = 1f)
                }
                Zoomable(
                    state = zoomableState,
                    onTap = {
                        onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
                    }
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        error = painterResource(id = R.drawable.image_not_found),
                        model = imagesList[imageIndex],
                        contentDescription = null,
                    )
                }
            }
        }
    }
}