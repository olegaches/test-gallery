package com.example.imagesproject.presentation.images_screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.*
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
    navController: NavController,
    viewModel: ImagesViewModel = hiltViewModel(),
) {
    Box(
        Modifier
            .fillMaxSize()
    ) {
        val state = viewModel.state.collectAsState().value
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        var isExpanded by remember {
            mutableStateOf(false)
        }
        var sizeOfImageOnTopLayout by remember { mutableStateOf(IntSize.Zero) }
        var positionOfImageOnTopLayout by remember { mutableStateOf(Offset.Zero) }
        var imageBitmapOnTopLayout by remember { mutableStateOf<ImageBitmap?>(null) }
        var imageIndexOnTopLayout by remember { mutableStateOf(0) }


        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    scrollBehavior = scrollBehavior,
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
                    //.padding(paddingValues)
                    .fillMaxSize()) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Adaptive(90.dp),
                ) {
                    items(state.imagesList.size) { index ->
                        val imageUrl = state.imagesList[index]
                        var sizeOfImage by remember { mutableStateOf(IntSize.Zero) }
                        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
                        var positionOfImage by remember { mutableStateOf(Offset.Zero) }
                        GlideImage(
                            success = { imageState ->
                                imageState.imageBitmap?.let {
                                    Image(
                                        bitmap = it,
                                        modifier = Modifier.size(90.dp),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillBounds,
                                    )
                                    imageBitmap = it
                                }
                            },
                            imageModel = { imageUrl },
                            modifier = Modifier
                                .padding(1.dp)
                                .onGloballyPositioned { coordinates ->
                                    sizeOfImage = coordinates.size
                                    positionOfImage = coordinates.boundsInWindow().topLeft
                                }
                                .clickable {
                                    sizeOfImageOnTopLayout = sizeOfImage
                                    positionOfImageOnTopLayout = positionOfImage
                                    imageBitmapOnTopLayout = imageBitmap
                                    imageIndexOnTopLayout = index
                                    isExpanded = true
                                },
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
                                viewModel.onDeleteFailedImage(imageUrl)
                            }
                        )
                    }
                }
            }
        }
        if(isExpanded) {
            CopyOfImage(positionOfImageOnTopLayout, state.imagesList, imageIndexOnTopLayout)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CopyOfImage(offset: Offset, imagesList: List<String>, imageIndex: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var onBackClicked by remember { mutableStateOf(false) }
        val statusBars = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navigationBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val maxWidth = LocalConfiguration.current.screenWidthDp.dp
        val maxHeight = (LocalConfiguration.current.screenHeightDp
        + statusBars.value + navigationBars.value + 1.0).dp
        var isAnimated by remember { mutableStateOf(false) }
        BackHandler() {
            onBackClicked = true
            isAnimated = !isAnimated
        }
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
        val rocketOffset by transition.animateOffset(transitionSpec = {
            if (this.targetState) {
                tween(1000) // launch duration

            } else {
                tween(1500) // land duration
            }
        }, label = "rocket offset") { animated ->
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
                    rocketOffset.round()
                }
                .animateContentSize()
                .onPlaced {
                    if(!onBackClicked)
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