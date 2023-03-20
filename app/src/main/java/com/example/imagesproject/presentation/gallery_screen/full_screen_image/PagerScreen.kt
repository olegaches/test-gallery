package com.example.imagesproject.presentation.gallery_screen.full_screen_image

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.core.util.Extension.shouldUseDarkTheme
import com.example.imagesproject.presentation.gallery_screen.AnimationType
import com.example.imagesproject.presentation.gallery_screen.PagerScreenState
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.components.ImageScreenBottomBar
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.components.ImageScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.TransparentSystemBars
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.components.DeleteAlertDialog
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.components.animatedImage
import com.example.imagesproject.ui.theme.ImagesProjectTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.skydoves.orbital.Orbital
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScreen(
    imagesList: List<String>,
    pagerScreenState: PagerScreenState,
    paddingValues: PaddingValues,
    onImageScreenEvent: (ImageScreenEvent) -> Unit,
) {
    ImagesProjectTheme(darkTheme = true) {
        if(!pagerScreenState.isVisible || imagesList.isEmpty()) {
            TransparentSystemBars(
                shouldUseDarkTheme(themeStyle = pagerScreenState.currentTheme),
            )
            rememberSystemUiController().isNavigationBarVisible = true
            return@ImagesProjectTheme
        }
        BackHandler {
            onImageScreenEvent(ImageScreenEvent.OnBackToGallery)
        }
        val systemUiController = rememberSystemUiController()
        val systemNavigationBarVisible = pagerScreenState.systemNavigationBarVisible
        LaunchedEffect(key1 = systemNavigationBarVisible) {
            systemUiController.isNavigationBarVisible = systemNavigationBarVisible
        }
        TransparentSystemBars(
            true,
        )
        val pagerIndex = pagerScreenState.pagerIndex
        val pagerState = rememberPagerState(
            initialPage = pagerIndex
        )
        val unknownException = stringResource(id = R.string.unknown_exception)
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        if(pagerScreenState.deleteDialogOpened) {
            DeleteAlertDialog(
                confirmButtonClick = {
                    onImageScreenEvent(ImageScreenEvent.OnDeleteImageUrl(pagerIndex))
                    onImageScreenEvent(ImageScreenEvent.OnDeleteDialogVisibilityChange(false))
                },
                onDismissRequest = { onImageScreenEvent(ImageScreenEvent.OnDeleteDialogVisibilityChange(false)) },
            )
        }
        val transparentColor = remember {
            Color.Transparent
        }
        val topBarVisible = pagerScreenState.topBarVisible
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = transparentColor,
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            bottomBar = {
                ImageScreenBottomBar(
                    imageUrl = imagesList[pagerIndex],
                    isVisible = topBarVisible,
                    onErrorOccurred = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message = unknownException)
                        }
                    }
                )
            },
            topBar = {
                ImageScreenTopBar(
                    isVisible = topBarVisible,
                    title = pagerScreenState.topBarText,
                    onBackClicked = {
                        onImageScreenEvent(ImageScreenEvent.OnBackToGallery)
                    },
                    onDeleteIconClick = {
                        onImageScreenEvent(ImageScreenEvent.OnDeleteDialogVisibilityChange(true))
                    }
                )
            }
        ) {
            val localConfiguration = LocalConfiguration.current
            val isHorizontalOrientation = localConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val isRightLayoutDirection = localConfiguration.layoutDirection == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
            val animationState = pagerScreenState.animationState
            val stateAnimationType = animationState.animationType
            val expandAnimationType = remember { AnimationType.EXPAND_ANIMATION }
            LaunchedEffect(key1 = true) {
                if(stateAnimationType != expandAnimationType) {
                    onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
                    onImageScreenEvent(ImageScreenEvent.OnAnimate(expandAnimationType))
                }
            }

            var animationType by remember {
                mutableStateOf(stateAnimationType)
            }
            LaunchedEffect(key1 = stateAnimationType) {
                animationType = stateAnimationType
            }
            val currentPage = pagerState.currentPage
            LaunchedEffect(key1 = currentPage) {
                onImageScreenEvent(
                    ImageScreenEvent.OnPagerIndexChanged(
                        currentPage
                    ))
            }
            val imageContent = animatedImage(
                pagerScreenState = pagerScreenState,
                imageUrl = imagesList[pagerIndex],
                isHorizontalOrientation = isHorizontalOrientation,
                isRightLayoutDirection = isRightLayoutDirection,
                paddingValues = paddingValues,
                animationType = animationType,
            )
            val backGroundColor by animateColorAsState(
                targetValue = if (animationType == expandAnimationType) Color.Black else transparentColor,
                animationSpec = tween(durationMillis = 270)
            )
            Box(
                modifier = Modifier
                    .background(backGroundColor)
                    .fillMaxSize()
            ) {
                if(animationState.isAnimationInProgress) {
                    Orbital(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        imageContent()
                    }
                } else if(animationType == expandAnimationType) {
                    var isTouching by remember {
                        mutableStateOf(false)
                    }
                    val imageListSize = imagesList.size
                    HorizontalPager(
                        state = pagerState,
                        pageCount = imageListSize,
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
                            val currentImageOffset = pagerState.currentPageOffsetFraction
                            if(currentImageOffset > 0.1f || currentImageOffset < -0.1f) {
                                coroutineScope.launch(Dispatchers.Main) {
                                    zoomableState.animateScaleTo(1f)
                                }
                            }
                        }
                        LaunchedEffect(key1 = currentPage) {
                            if(imageSize != null && currentPage == index)
                                onImageScreenEvent(
                                    ImageScreenEvent.OnPagerCurrentImageChange(
                                        imageSize!!
                                    ))
                        }
                        LaunchedEffect(key1 = imageListSize) {
                            if(imageSize != null && currentPage == index)
                                onImageScreenEvent(
                                    ImageScreenEvent.OnPagerCurrentImageChange(
                                        imageSize!!
                                    ))
                        }
                        val zoomableStateScale = zoomableState.scale
                        LaunchedEffect(key1 = zoomableStateScale) {
                            if(zoomableStateScale <= 0.5) {
                                onImageScreenEvent(ImageScreenEvent.OnCurrentScaleChange(zoomableStateScale))
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
                            val imageNotFoundId = remember {
                                R.drawable.image_not_found
                            }
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
                                error = painterResource(id = imageNotFoundId),
                                model = imagesList[index],
                                contentScale = ContentScale.Fit,
                                placeholder = painterResource(id = imageNotFoundId),
                                onError = { painterState ->
                                    imageSize = painterState.painter?.intrinsicSize
                                },
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
    }
}