package com.example.imagesproject.presentation.gallery_screen.components

import android.app.PendingIntent
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.MainActivity
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.ImageScreenState
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagesList: List<String>,
    imageScreenState: ImageScreenState,
    paddingValues: PaddingValues,
    onImageScreenEvent: (ImageScreenEvent) -> Unit,
) {
    if(!imageScreenState.isVisible)
        return

    val isHorizontalOrientation = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isRightLayoutDirection= LocalConfiguration.current.layoutDirection == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
    LaunchedEffect(key1 = true) {
        if(imageScreenState.animationState.animationType != AnimationType.EXPAND_ANIMATION) {
            onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
            onImageScreenEvent(ImageScreenEvent.OnAnimate(AnimationType.EXPAND_ANIMATION))
        }
    }
    val imageIndexesList = imageScreenState.imageIndexesList
    var currentScale by remember {
        mutableStateOf(1f)
    }

    var animationType by remember {
        mutableStateOf(imageScreenState.animationState.animationType)
    }
    LaunchedEffect(key1 = imageScreenState.animationState.animationType) {
        animationType = imageScreenState.animationState.animationType
    }
    val pagerState = rememberPagerState(
        initialPage = imageScreenState.pagerIndex
    )
    val applicationContext = LocalContext.current
    LaunchedEffect(key1 = pagerState.currentPage) {
        val index = imageIndexesList[pagerState.currentPage]
        onImageScreenEvent(ImageScreenEvent.OnTopBarTitleTextChange(imagesList[index]))
        val resultIntent = Intent(applicationContext, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE)
        onImageScreenEvent(
            ImageScreenEvent.OnShowNotification(
                NotificationCompat.Builder(
                    applicationContext,
                    Constants.NOTIFICATION_CHANNEL_ID
                    )
                    .setOngoing(true)
                    .setSilent(true)
                    .setContentText(imagesList[index])
                    .setSmallIcon(R.drawable.ic_android_24)
                    .setContentIntent(resultPendingIntent)
                    .build()
            )
        )
        onImageScreenEvent(ImageScreenEvent.OnPagerIndexChanged(
            pagerState.currentPage
        ))
    }
    val imageContent = rememberContentWithOrbitalScope {
        val orbitalScope = this
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val ratio = imageScreenState.painterIntrinsicSize.width/imageScreenState.painterIntrinsicSize.height
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
                        .offset { imageScreenState.imageOffset.toIntOffset() }
                        .size(
                            imageScreenState.gridItemSize.width.dp,
                            imageScreenState.gridItemSize.width.dp
                        )
                }.animateSharedElementTransition(
                    orbitalScope,
                    SpringSpec(stiffness = 1200f),
                    SpringSpec(stiffness = 1200f)
                ),
                model = imagesList[imageIndexesList[imageScreenState.pagerIndex]],
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
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
                modifier = Modifier
                    .fillMaxSize()
            ) {
                imageContent()
            }
        } else if(animationType == AnimationType.EXPAND_ANIMATION) {
            var isTouching by remember {
                mutableStateOf(false)
            }
            var prevPagerIndex by remember {
                mutableStateOf(pagerState.currentPage)
            }
            HorizontalPager(
                state = pagerState,
                pageCount = imageIndexesList.size,
                modifier = Modifier
                    .fillMaxSize(),
                pageSpacing = 16.dp,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(0)
                ),
                contentPadding = PaddingValues(0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { index ->
                val imageIndex = imageIndexesList[index]
                val overZoomConfig = OverZoomConfig(
                    minSnapScale = 1f,
                    maxSnapScale = 1.7f
                )
                val zoomableState = rememberZoomableState(
                    initialScale = 1f,
                    minScale = 0.1f,
                    overZoomConfig = overZoomConfig,
                )
                var imageSize by remember {
                    mutableStateOf<Size?>(null)
                }
                LaunchedEffect(key1 = isTouching) {
                    if(prevPagerIndex != pagerState.currentPage && !isTouching) {
                        launch(Dispatchers.Main) {
                            zoomableState.animateScaleTo(1f)
                            prevPagerIndex = pagerState.currentPage
                        }
                    }
                }
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        if(imageSize != null && pagerState.currentPage == index)
                            onImageScreenEvent(ImageScreenEvent.OnPagerCurrentImageChange(
                                imageIndex,
                                imageSize!!
                            ))
                    }
                }
                LaunchedEffect(key1 = zoomableState.scale) {
                    if(zoomableState.scale <= 0.5) {
                        currentScale = zoomableState.scale
                        if(!isTouching) {
                            onImageScreenEvent(ImageScreenEvent.OnBackToGallery)
                        }
                    }
                }
                Zoomable(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                do {
                                    val event = awaitPointerEvent()
                                    isTouching = true
                                    if (event.type == PointerEventType.Release) {
                                        isTouching = false
                                    }
                                } while (event.changes.any { it.pressed })
                            }
                        },
                    state = zoomableState,
                    onTap = {
                        onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
                    },
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .then(
                                if (imageSize != null) {
                                    Modifier.aspectRatio(
                                        imageSize!!.width / imageSize!!.height,
                                        isHorizontalOrientation
                                    )
                                } else
                                    Modifier.fillMaxSize()
                            ),
                        model = imagesList[imageIndex],
                        contentScale = ContentScale.Fit,
                        onSuccess = { painterState ->
                            imageSize = painterState.painter.intrinsicSize
                        },
                        contentDescription = null,
                    )
                }
            }
        }
    }
}