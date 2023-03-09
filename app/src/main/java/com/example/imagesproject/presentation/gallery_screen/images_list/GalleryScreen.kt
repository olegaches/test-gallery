package com.example.imagesproject.presentation.gallery_screen.images_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.domain.type.Screen
import com.example.imagesproject.presentation.gallery_screen.components.ErrorLabel
import com.example.imagesproject.presentation.gallery_screen.components.GalleryScreenTopBar
import com.example.imagesproject.presentation.gallery_screen.full_screen_image.PagerScreen
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    navController: NavController,
    viewModel: ImagesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    var savedPaddingValues by remember {
        mutableStateOf(PaddingValues(0.dp))
    }
    Scaffold(
        topBar = {
            GalleryScreenTopBar(
                onThemeSettingsClick = { navController.navigate(Screen.ThemeSettingsScreen.route) }
            )
        }
    ) { paddingValues ->
        BackHandler(enabled = state.imageScreenState.isVisible) {
            viewModel.onBackClicked()
        }
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
    if(state.error != null) {
        ErrorLabel(
            error = state.error,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
            ,
            onRefreshClick = viewModel::onRefresh
        )
    }
    if(state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    PagerScreen(
        paddingValues = savedPaddingValues,
        imagesList = state.imagesList,
        imageScreenState = state.imageScreenState,
        onImageScreenEvent = viewModel::onImageScreenEvent,
    )
}