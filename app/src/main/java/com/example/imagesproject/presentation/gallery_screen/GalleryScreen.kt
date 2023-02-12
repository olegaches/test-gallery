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
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imagesproject.presentation.gallery_screen.components.GalleryScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.ImageScreen
import com.example.imagesproject.presentation.gallery_screen.components.ImageScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.LazyGridImages
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
                    LazyGridImages(
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
                    onImageScreenEvent = viewModel::onImageScreenEvent,
                )
            }
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