package com.example.imagesproject.presentation.gallery_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.presentation.gallery_screen.components.GalleryScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.ImageScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.ui_events.GalleryScreenEvent
import com.example.imagesproject.presentation.gallery_screen.ui_events.ImageScreenEvent
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: ImagesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = state.systemNavigationBarVisible) {
        systemUiController.isNavigationBarVisible = state.systemNavigationBarVisible
    }
    SideEffect {
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ImageScreenTopBar(
                isVisible = state.topBarVisible,
                onBackClicked = viewModel::onBackClicked
            )
        }
    ) {
        Box(
            Modifier
                .background(Color.Blue)
                .fillMaxSize()
        ) {
            var savedPaddingValues by remember {
                mutableStateOf(PaddingValues(0.dp))
            }
            BackHandler {
                viewModel.onBackClicked()
            }
            Scaffold(
                topBar = {
                    GalleryScreenTopBar()
                }
            ) { paddingValues ->
                savedPaddingValues = paddingValues
                Box(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    HorizontalLazyGridImages(
                        lazyGridState = state.lazyGridState,
                        imagesUrlList = state.imagesList,
                        onGalleryScreenEvent = viewModel::onGalleryScreenEvent
                    )
                }
            }
            if(state.imageScreenState.isVisible) {
                ImageScreen(
                    paddingValues = savedPaddingValues,
                    imagesList = state.imagesList,
                    imageScreenState = state.imageScreenState,
                    onImageClick = viewModel::onBarsVisibilityChange,
                    savePagerIndex = viewModel::savePagerIndex,
                    onImageScreenEvent = viewModel::onImageScreenEvent,
                )
            }
        }
    }
}

@Composable
fun HorizontalLazyGridImages(
    lazyGridState: LazyGridState,
    imagesUrlList: List<String>,
    onGalleryScreenEvent: (GalleryScreenEvent) -> Unit,
) {
    val context = LocalContext.current
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
        ,
        state = lazyGridState,
        columns = GridCells.Fixed(4),
    ) {
        items(imagesUrlList.size) { index ->
            var isSuccess by remember {
                mutableStateOf(false)
            }
            val imageUrl = imagesUrlList[index]
            AsyncImage(
                model = imageUrl,
                modifier = Modifier
                    .padding(1.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable(
                        enabled = isSuccess,
                        onClick = {
                            val visibleItems =
                                lazyGridState.layoutInfo.visibleItemsInfo
                            val lastElement = visibleItems.last()
                            val lastVisibleColumn = visibleItems.last().column + 1
                            val lastFullVisibleIndex =
                                lastElement.index - lastVisibleColumn
                            val firstFullVisibleIndex =
                                visibleItems.first().index + lastVisibleColumn - 1// - lastVisibleColumn
                            val offset =
                                lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
                            val intSizeDp = convertPixelsToDp(
                                context = context,
                                size = lastElement.size.toSize()
                            )
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemOffsetToScroll(offset))
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridItemSize(intSizeDp))
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveCurrentGridItemOffset(index))
                            onGalleryScreenEvent(GalleryScreenEvent.OnSaveGridVisibleInterval(
                                startIndex = if (firstFullVisibleIndex < 0) lastVisibleColumn - 1 else firstFullVisibleIndex,
                                endIndex = if (lastFullVisibleIndex < 0) 0 else lastFullVisibleIndex,
                            ))
                            onGalleryScreenEvent(GalleryScreenEvent.OnImageClick(index))
                        }
                    ),
                onSuccess = {
                    isSuccess = true
                },
                contentScale = ContentScale.FillWidth,
                error = painterResource(id = R.drawable.image_not_found),
                contentDescription = null,
            )
        }
    }
}

fun convertPixelsToDp(size: Size, context: Context?): IntSize {
    val padding = 0
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val height = size.height / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        val width = size.width / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        IntSize(width = width.toInt() - padding, height = height.toInt() - padding) // '-2' is for padding matching
    }
    else {
        val metrics = Resources.getSystem().displayMetrics
        val height = size.height / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        val width = size.width / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        IntSize(width = width.toInt() - padding, height = height.toInt() - padding) // '-2' is for padding matching
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagesList: List<String>,
    imageScreenState: ImageScreenState,
    paddingValues: PaddingValues,
    onImageClick:() -> Unit,
    onImageScreenEvent: (ImageScreenEvent) -> Unit,
    savePagerIndex: (Int) -> Unit,
) {

    LaunchedEffect(key1 = true) {
        onImageClick()
        onImageScreenEvent(ImageScreenEvent.OnAnimate(AnimationType.EXPAND_ANIMATION))
    }

    var animationType by remember {
        mutableStateOf(imageScreenState.animationState.animationType)
    }
    LaunchedEffect(key1 = imageScreenState.animationState.animationType) {
        animationType = imageScreenState.animationState.animationType
    }

    val pagerState = rememberPagerState(
        initialPage = imageScreenState.imageIndex
    )

    LaunchedEffect(key1 = pagerState.currentPage) {
        onImageScreenEvent(ImageScreenEvent.OnGridItemOffsetChange(pagerState.currentPage))
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
            model = imagesList[imageScreenState.imageIndex]
            ,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
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
                onImageScreenEvent(ImageScreenEvent.OnPagerIndexChanged(pagerState.currentPage))
                if(pagerState.currentPage >= imageScreenState.visibleGridInterval.second || pagerState.currentPage <= imageScreenState.visibleGridInterval.first) {
                    savePagerIndex(pagerState.currentPage)
                }
            }
            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 3,
                pageCount = imagesList.size,
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
                val zoomableState = rememberZoomableState()
                LaunchedEffect(key1 = pagerState.targetPage) {
                    zoomableState.animateScaleTo(targetScale = 1f)
                }
                Zoomable(
                    state = zoomableState,
                    onTap = {
                        onImageClick()
                    }
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        error = painterResource(id = R.drawable.image_not_found),
                        model = imagesList [index],
                        contentDescription = null,
                    )
                }
            }
        }
    }
}