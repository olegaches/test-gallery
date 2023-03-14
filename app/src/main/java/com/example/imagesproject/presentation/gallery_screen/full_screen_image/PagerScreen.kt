package com.example.imagesproject.presentation.gallery_screen.full_screen_image

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
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
import androidx.compose.ui.unit.LayoutDirection
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
import com.example.imagesproject.ui.theme.ImagesProjectTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
            return@ImagesProjectTheme
        }
        BackHandler {
            onImageScreenEvent(ImageScreenEvent.OnBackToGallery)
        }
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(key1 = pagerScreenState.systemNavigationBarVisible) {
            systemUiController.isNavigationBarVisible = pagerScreenState.systemNavigationBarVisible
        }
        TransparentSystemBars(
            true,
        )
        val pagerState = rememberPagerState(
            initialPage = pagerScreenState.pagerIndex
        )
        var isDeleteDialogOpened by remember {
            mutableStateOf(false)
        }
        val unknownException = stringResource(id = R.string.unknown_exception)
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        if(isDeleteDialogOpened) {
            DeleteAlertDialog(
                confirmButtonClick = {
                    onImageScreenEvent(ImageScreenEvent.OnDeleteImageUrl(pagerScreenState.pagerIndex))
                    isDeleteDialogOpened = false
                },
                onDismissRequest = { isDeleteDialogOpened = false },
            )
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            bottomBar = {
                ImageScreenBottomBar(
                    imageUrl = imagesList[pagerScreenState.pagerIndex],
                    isVisible = pagerScreenState.topBarVisible,
                    onErrorOccurred = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message = unknownException)
                        }
                    }
                )
            },
            topBar = {
                ImageScreenTopBar(
                    isVisible = pagerScreenState.topBarVisible,
                    title = pagerScreenState.topBarText,
                    onBackClicked = {
                        onImageScreenEvent(ImageScreenEvent.OnBackToGallery)
                    },
                    onDeleteIconClick = {
                        isDeleteDialogOpened = true
                    }
                )
            }
        ) {
            val isHorizontalOrientation = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            val isRightLayoutDirection = LocalConfiguration.current.layoutDirection == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
            LaunchedEffect(key1 = true) {
                if(pagerScreenState.animationState.animationType != AnimationType.EXPAND_ANIMATION) {
                    onImageScreenEvent(ImageScreenEvent.OnBarsVisibilityChange)
                    onImageScreenEvent(ImageScreenEvent.OnAnimate(AnimationType.EXPAND_ANIMATION))
                }
            }
            var currentScale by remember {
                mutableStateOf(1f)
            }

            var animationType by remember {
                mutableStateOf(pagerScreenState.animationState.animationType)
            }
            LaunchedEffect(key1 = pagerScreenState.animationState.animationType) {
                animationType = pagerScreenState.animationState.animationType
            }
            LaunchedEffect(key1 = pagerState.currentPage) {
                onImageScreenEvent(
                    ImageScreenEvent.OnPagerIndexChanged(
                        pagerState.currentPage
                    ))
            }
            val imageContent = rememberContentWithOrbitalScope {
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
                        model = imagesList[pagerScreenState.pagerIndex],
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
                if(pagerScreenState.animationState.isAnimationInProgress) {
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
                        pageCount = imagesList.size,
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
                                    onImageScreenEvent(
                                        ImageScreenEvent.OnPagerCurrentImageChange(
                                            imageSize!!
                                        ))
                            }
                        }
                        LaunchedEffect(key1 = imagesList.size) {
                            if(imageSize != null && pagerState.currentPage == index)
                                onImageScreenEvent(
                                    ImageScreenEvent.OnPagerCurrentImageChange(
                                        imageSize!!
                                    ))
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
                                error = painterResource(id = R.drawable.image_not_found),
                                model = imagesList[index],
                                contentScale = ContentScale.Fit,
                                placeholder = painterResource(id = R.drawable.image_not_found),
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