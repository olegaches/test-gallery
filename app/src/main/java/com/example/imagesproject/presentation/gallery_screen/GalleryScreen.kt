package com.example.imagesproject.presentation.gallery_screen

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.domain.type.Screen
import com.example.imagesproject.presentation.gallery_screen.components.GalleryScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.ImageScreen
import com.example.imagesproject.presentation.gallery_screen.components.ImageScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.components.LazyGridImages
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.collections.immutable.toImmutableList

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
            ImageScreenTopBar(
                isVisible = state.topBarVisible,
                title = state.topBarTitleText,
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
            BackHandler(enabled = state.imageScreenState.isVisible) {
                viewModel.onBackClicked()
            }
            Scaffold(
                topBar = {
                    GalleryScreenTopBar(
                        onThemeSettingsClick = { navController.navigate(Screen.ThemeSettingsScreen.route) }
                    )
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
                        imagesUrlList = state.imagesList.toImmutableList(),
                        onGalleryScreenEvent = viewModel::onGalleryScreenEvent
                    )
                }
            }
            ImageScreen(
                imagesList = state.imagesList,
                paddingValues = savedPaddingValues,
                imageScreenState = state.imageScreenState,
                onImageScreenEvent = viewModel::onImageScreenEvent,
            )
        }
    }
}