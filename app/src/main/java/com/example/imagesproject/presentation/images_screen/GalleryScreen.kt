package com.example.imagesproject.presentation.images_screen

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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.domain.model.ImageItem
import com.example.imagesproject.presentation.Constants
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
            AnimatedVisibility(
                visible = state.topBarVisible,
                enter = fadeIn(
                    animationSpec = tween(Constants.TOP_BAR_VISIBILITY_ANIMATION_TIME)
                ),
                exit = fadeOut(
                    animationSpec = tween(Constants.TOP_BAR_VISIBILITY_ANIMATION_TIME)
                )
            ) {
                CenterAlignedTopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black,
                        navigationIconContentColor = Color.White,
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onBackClicked()
                            }
                        ) {
                            Icon(
                                contentDescription = null,
                                imageVector = Icons.Default.ArrowBack
                            )
                        }
                    }
                )
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Blue)
        ) {
            BackHandler() {
                viewModel.onBackClicked()
            }
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.top_bar_title),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                    )
                }
            ) { paddingValues ->
                Box(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    val context = LocalContext.current
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                                ,
                        state = state.lazyGridState,
                        columns = GridCells.Fixed(4),
                    ) {
                        items(state.imagesList.size) { index ->
                            val imageUrl = state.imagesList[index].url
                            AsyncImage(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .onGloballyPositioned { coordinates ->
                                        viewModel.setItemOffset(
                                            index,
                                            coordinates.boundsInWindow().topLeft
                                        )
                                    }
                                    .clickable(
                                        onClick = {
                                            val visibleItems =
                                                state.lazyGridState.layoutInfo.visibleItemsInfo
                                            val lastVisibleRow = visibleItems.last().row + 1
                                            val lastElement = visibleItems.last()
                                            val lastVisibleColumn = visibleItems.last().column + 1
                                            val lastFullVisibleIndex =
                                                lastElement.index - lastVisibleColumn
                                            val firstFullVisibleIndex =
                                                visibleItems.first().index + lastVisibleColumn - 1 - lastVisibleColumn
                                            val offset =
                                                state.lazyGridState.layoutInfo.viewportSize.height - lastElement.size.height
                                            val visibleGridSize = lastVisibleRow * lastVisibleColumn
                                            val visibleParams = GridLayoutParams(
                                                visibleRows = lastVisibleRow,
                                                visibleColumns = lastVisibleColumn,
                                                visibleGridSize = visibleGridSize,
                                                itemOffset = offset,
                                                lastFullVisibleIndex = if (lastFullVisibleIndex < 0) 0 else lastFullVisibleIndex,
                                                firstFullVisibleIndex = if (firstFullVisibleIndex < 0) lastVisibleColumn - 1 else firstFullVisibleIndex,
                                            )
                                            val intSizeDp = convertPixelsToDp(
                                                context = context,
                                                size = lastElement.size.toSize()
                                            )
                                            viewModel.saveGridItemSize(intSizeDp)
                                            viewModel.saveLayoutParams(visibleParams)
                                            viewModel.onImageClicked(index)
                                        }
                                    )
                                ,
                                contentScale = ContentScale.FillBounds,
                                model = imageUrl,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
            if(state.gridLayoutParams != null && state.imageScreenState.isVisible) {
                ImageScreen(
                    gridLayoutParams = state.gridLayoutParams,
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

fun convertPixelsToDp(size: Size, context: Context?): IntSize {
    return if(context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val height = size.height / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        val width = size.width / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        IntSize(width = width.toInt() - 2, height = height.toInt() - 2) // '-2' is for padding matching
    }
    else {
        val metrics = Resources.getSystem().displayMetrics
        val height = size.height / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        val width = size.width / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        IntSize(width = width.toInt() - 2, height = height.toInt() - 2) // '-2' is for padding matching
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagesList: List<ImageItem>,
    gridLayoutParams: GridLayoutParams,
    imageScreenState: ImageScreenState,
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

    val imageContent = rememberContentWithOrbitalScope {
        val imageItem = imagesList[imageScreenState.imageIndex]
        AsyncImage(
            modifier = if (animationType == AnimationType.EXPAND_ANIMATION) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .offset {
                        imageItem.offset?.round() ?: Offset.Zero.round()
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
            model = imageItem.url,
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )
    }
    if(imageScreenState.animationState.isAnimationInProgress) {
        Orbital(
            modifier = Modifier.fillMaxSize()
        ) {
            imageContent()
        }
    } else {
        LaunchedEffect(key1 = pagerState.currentPage) {
            onImageScreenEvent(ImageScreenEvent.OnPagerIndexChanged(pagerState.currentPage))
            if(pagerState.currentPage >= gridLayoutParams.lastFullVisibleIndex || pagerState.currentPage <= gridLayoutParams.firstFullVisibleIndex) {
                savePagerIndex(pagerState.currentPage)
            }
        }
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 3,
            pageCount = imagesList.size,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
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
                        .fillMaxSize()
                    ,
                    contentScale = ContentScale.FillBounds,
                    model = imagesList[index].url,
                    contentDescription = null,
                )
            }
        }
    }
}