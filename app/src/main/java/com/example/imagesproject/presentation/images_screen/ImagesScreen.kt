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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.*
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
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
                            onClick = viewModel::onBackClicked
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
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Adaptive(90.dp),
                    ) {
                        items(state.imagesList.size) { index ->
                            var globalImageOffset by remember {
                                mutableStateOf(Offset.Zero)
                            }
                            var isSuccess by remember {
                                mutableStateOf(true)
                            }
                            val imageUrl = state.imagesList[index]
                            GlideImage(
                                success = { imageState ->
                                    imageState.imageBitmap?.let {
                                        Image(
                                            bitmap = it,
                                            modifier = Modifier.size(100.dp),
                                            contentDescription = null,
                                            contentScale = ContentScale.FillBounds,
                                        )
                                    }
                                },
                                imageModel = { imageUrl },
                                modifier = Modifier
                                    .padding(1.dp)
                                    .onGloballyPositioned { coordinates ->
                                        globalImageOffset = coordinates.boundsInWindow().topLeft
                                    }
                                    .clickable(
                                        enabled = isSuccess,
                                        onClick = {
                                            viewModel.onImageClicked(index, globalImageOffset)
                                        }
                                    )
                                    ,
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.FillBounds,
                                ),
                                component = rememberImageComponent {
                                    +ShimmerPlugin(
                                        baseColor = MaterialTheme.colorScheme.background,
                                        highlightColor = Color.LightGray,
                                    )
                                    +PalettePlugin(
                                        imageModel = { imageUrl },
                                        useCache = true,
                                    )
                                },
                                failure = {
                                    isSuccess = false
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(Color.LightGray)
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                ,
                                            text = stringResource(id = R.string.image_loading_error_text),
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            if (state.isExpanded) {
                state.clickedImageGlobalOffset?.let {
                    CopyOfImage(
                        it,
                        state.imagesList,
                        state.currentImageIndex,
                        viewModel::onBarsVisibilityChange,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CopyOfImage(
    offset: Offset,
    imagesList: List<String>,
    imageIndex: Int,
    onImageClick :() -> Unit,
) {
    LaunchedEffect(key1 = true) {
        onImageClick()
    }
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = when {
                        scale * zoom < 1f -> scale
                        zoom * scale > 3f -> scale
                        else -> scale * zoom
                    }
                }
            }
            .combinedClickable(
                onClick = onImageClick
            )
    ) {
        val statusBars = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navigationBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val localConfig = LocalConfiguration.current
        val maxWidth = remember { localConfig.screenWidthDp.dp }
        val maxHeight = remember { (localConfig.screenHeightDp
                + statusBars.value + navigationBars.value + 1.0).dp }
        var isAnimated by remember { mutableStateOf(false) }
        val transition = updateTransition(targetState = isAnimated, label = "transition")
        val sizeHeight by transition.animateDp(transitionSpec = {
            tween(1000)
        }, "") { animated ->
            if (animated) maxHeight else 90.dp
        }
        val sizeWidth by transition.animateDp(transitionSpec = {
            tween(1000)
        }, "") { animated ->
            if (animated) maxWidth else 90.dp
        }
        val imageOffset by transition.animateOffset(transitionSpec = {
            if (this.targetState) {
                tween(1000) // launch duration

            } else {
                tween(1000) // land duration
            }
        }, label = "image offset") { animated ->
            if (animated) Offset(0f, 0f) else offset
        }

        val pagerState = rememberPagerState(
            initialPage = imageIndex
        )
        HorizontalPager(
            state = pagerState,
            pageCount = imagesList.size,
            modifier = Modifier
                .size(sizeWidth, sizeHeight)
                .offset {
                    imageOffset.round()
                }
                .animateContentSize()
                .onPlaced {
                    isAnimated = true
                }
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) { index ->
            val imageUrl = imagesList[index]
            Log.e("66", imageUrl + " " + index)
            GlideImage(
                success = { imageState ->
                    imageState.imageBitmap?.let {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                            ,
                            bitmap = it,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                        )
                    }
                },
                imageModel = { imageUrl },
                component = rememberImageComponent {
                    +ShimmerPlugin(
                        baseColor = MaterialTheme.colorScheme.background,
                        highlightColor = Color.LightGray,
                    )
                    +PalettePlugin(
                        imageModel = { imageUrl },
                        useCache = true,
                    )
                },
            )
        }
    }
}