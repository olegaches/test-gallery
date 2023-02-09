package com.example.imagesproject.presentation.images_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.domain.model.ImageItem
import com.example.imagesproject.presentation.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
import kotlinx.coroutines.delay
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    navController: NavController,
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
                    var gridHeight by remember {
                        mutableStateOf(0)
                    }
                    LazyVerticalGrid(
                        state = state.lazyGridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned {
                                gridHeight = it.size.height
                            },
                        columns = GridCells.Adaptive(90.dp),
                    ) {
                        items(state.imagesList.size) { index ->
                            var globalImageOffset by remember {
                                mutableStateOf<Offset?>(null)
                            }
                            LaunchedEffect(key1 = globalImageOffset) {
                                globalImageOffset?.let { notNullableOffset ->
                                    viewModel.setItemOffset(index,
                                        notNullableOffset
                                    )
                                }
                            }
                            var isSuccess by remember {
                                mutableStateOf(true)
                            }
                            val imageUrl = state.imagesList[index].url
                            AsyncImage(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .size(100.dp)
                                    .onGloballyPositioned { coordinates ->
                                        globalImageOffset = coordinates.boundsInWindow().topLeft
                                    }
                                    .clickable(
                                        enabled = isSuccess,
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
                                            val offset = gridHeight - lastElement.size.height
                                            val visibleGridSize = lastVisibleRow * lastVisibleColumn
                                            val visibleParams = GridLayoutParams(
                                                visibleRows = lastVisibleRow,
                                                visibleColumns = lastVisibleColumn,
                                                visibleGridSize = visibleGridSize,
                                                itemOffset = offset,
                                                lastFullVisibleIndex = if (lastFullVisibleIndex < 0) 0 else lastFullVisibleIndex,
                                                firstFullVisibleIndex = if (firstFullVisibleIndex < 0) lastVisibleColumn - 1 else firstFullVisibleIndex,
                                            )
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
                    onHideImageLayer = viewModel::onHideImageLayer,
                    onImageScreenEvent = viewModel::onImageScreenEvent,
                )
            }
        }
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
    onHideImageLayer: () -> Unit,
) {
    LaunchedEffect(key1 = true) {
        onImageClick()
        onImageScreenEvent(ImageScreenEvent.OnAnimate(AnimationType.EXPAND_ANIMATION))
    }

    var isTransformed by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = imageScreenState.animationState.animationType == AnimationType.EXPAND_ANIMATION) {
        isTransformed != isTransformed
    }

    val pagerState = rememberPagerState(
        initialPage = imageScreenState.imageIndex
    )

    val imageContent = rememberContentWithOrbitalScope {
        val imageItem = imagesList[imageScreenState.imageIndex]
        AsyncImage(
            modifier = if (imageScreenState.animationState.animationType == AnimationType.EXPAND_ANIMATION) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .offset {
                        imageItem.offset!!.round()
                    }
                    .size(100.dp, 100.dp)
            }.animateSharedElementTransition(
                this,
                SpringSpec(stiffness = 500f),
                SpringSpec(stiffness = 500f)
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
        var scale by remember { mutableStateOf(1f) }
        val transition = updateTransition(targetState = imageScreenState.isAnimatedScale, label = "transition")
        val animatedScale by transition.animateFloat(transitionSpec = {
            tween(1000)
        }, "") { animated ->
            if (animated) {
                1f
            } else
                scale
        }
        HorizontalPager(
            state = pagerState,
            pageCount = imagesList.size,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        when {
                            scale * zoom < 0.5f -> scale *= zoom
                            zoom * scale > 3f -> scale *= zoom
                            scale * zoom < 1f -> onImageScreenEvent(
                                ImageScreenEvent.IsAnimatedScaleChanged(
                                    true
                                )
                            )
                            else -> scale *= zoom
                        }
                    }
                }
                .combinedClickable(
                    onClick = onImageClick
                )
            ,
            pageSpacing = 12.dp,
            flingBehavior =  PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(0)
            )
            ,
            contentPadding = PaddingValues(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) { index ->
            LaunchedEffect(key1 = index) {
                scale = 1f
            }
            Log.e("66", imagesList[index].url + " " + index)
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                ,
                contentScale = ContentScale.FillBounds,
                model = imagesList[index].url,
                contentDescription = null,
            )
        }
    }
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            //.clickable { isTransformed = !isTransformed }
//    ) {
//        HorizontalPager(
//            state = pagerState,
//            pageCount = imagesList.size,
//            modifier = Modifier
//                .fillMaxSize(),
//            flingBehavior =  PagerDefaults.flingBehavior(
//                state = pagerState,
//                pagerSnapDistance = PagerSnapDistance.atMost(0)
//            )
//            ,
//            contentPadding = PaddingValues(0.dp),
//            pageSpacing = 0.dp,
//            verticalAlignment = Alignment.CenterVertically,
//        ) { index ->
//            pagerIndex = index
//            Orbital(
//                isTransformed = isTransformed,
//                onStartContent = {
//
//                },
//                onTransformedContent = {
//                    HorizontalPager(pageCount = 3) {
//
//                    }
//                }
//            )
                //onTransformedContent
//                modifier = Modifier
//                    .clickable { isTransformed = !isTransformed },
//            ) {
//                imageContent()
//            }
//        }
//    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .graphicsLayer {
//                scaleX = scale
//                scaleY = scale
//            }
//            .pointerInput(Unit) {
//                detectTransformGestures { _, _, zoom, _ ->
//                    scale = when {
//                        scale * zoom < 1f -> scale
//                        zoom * scale > 3f -> scale
//                        else -> scale * zoom
//                    }
//                }
//            }
//            .combinedClickable(
//                onClick = onImageClick
//            )
//    ) {
//        val statusBars = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
//        val navigationBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
//        val localConfig = LocalConfiguration.current
//        val maxWidth = remember { localConfig.screenWidthDp.toFloat() }
//        val maxHeight = remember { (localConfig.screenHeightDp
//                + statusBars.value + navigationBars.value + 1.0f) }
//        val transition = updateTransition(targetState = isAnimated, label = "transition")
//        val animatedSize by transition.animateSize(transitionSpec = {
//            tween(1000)
//        }, "") { animated ->
//            if (animated) Size(maxWidth, maxHeight) else Size(100f, 100f)
//        }
//
//        val pagerState = rememberPagerState(
//            initialPage = imageIndex
//        )
//
//        HorizontalPager(
//            state = pagerState,
//            pageCount = imagesList.size,
//            modifier = Modifier
//                .fillMaxSize(),
//            flingBehavior =  PagerDefaults.flingBehavior(
//                state = pagerState,
//                pagerSnapDistance = PagerSnapDistance.atMost(0)
//            )
//                    ,
//            contentPadding = PaddingValues(0.dp),
//            pageSpacing = 0.dp,
//            verticalAlignment = Alignment.CenterVertically,
//        ) { index ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    ,
//            ) {
//                LaunchedEffect(key1 = index) {
//                    if(index >= gridLayoutParams.lastFullVisibleIndex || index <= gridLayoutParams.firstFullVisibleIndex) {
//                        savePagerIndex(index)
//                    }
//
//                }
//                val imageItem = imagesList[index]
//                val imageOffset by transition.animateOffset(transitionSpec = {
//                    if (this.targetState) {
//                        tween(800) // launch duration
//
//                    } else {
//                        tween(800) // land duration
//                    }
//                }, label = "image offset") { animated ->
//
//                    if (animated) {
//                        Offset.Zero
//                    } else {
//                        imageItem.offset ?: Offset.Zero
//                    }
//                }
//                Log.e("66", imageItem.url + " " + index)
//                AsyncImage(
//                    modifier = Modifier
//                        .align(Alignment.TopStart)
//                        .size(animatedSize.width.dp, animatedSize.height.dp)
//                        .offset {
//                            imageOffset.round()
//                        }
//                        .animateContentSize()
//                        .onGloballyPositioned { layoutCoordinates ->
//                            if (layoutCoordinates.positionInRoot() == imageItem.offset && !isExpanded) {
//                                onHideImageLayer()
//                            }
//                        }
//                    ,
//                    contentScale = ContentScale.FillBounds,
//                    model = imageItem.url,
//                    contentDescription = null,
//                )
//            }
//        }
//    }
}