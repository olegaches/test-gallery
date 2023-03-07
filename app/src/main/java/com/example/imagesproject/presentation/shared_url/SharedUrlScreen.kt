package com.example.imagesproject.presentation.shared_url

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.imagesproject.R
import com.example.imagesproject.domain.type.Screen
import com.example.imagesproject.presentation.gallery_screen.components.TransparentSystemBars
import com.example.imagesproject.presentation.shared_url.components.SharedUrlScreenBottomBar
import com.example.imagesproject.presentation.shared_url.components.SharedUrlScreenTopBar
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun SharedUrlScreen(
    url: String,
    navController: NavController,
    viewModel: SharedUrlViewModel = hiltViewModel()
) {
    TransparentSystemBars(true)
    val state = viewModel.state.collectAsState().value
    var isSuccess by remember {
        mutableStateOf(true)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            if(!isLoading) {
                SharedUrlScreenBottomBar(
                    isSuccess = isSuccess,
                    onSaveImage = {
                        viewModel.onSaveImage(url)
                        navController.navigate(Screen.ImagesScreen.route) {
                            popUpTo(Screen.SharedUrlScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    isVisible = state.visibleBars,
                    onCancel = { (context as? Activity)?.finish() }
                )
            }
        },
        topBar = {
            SharedUrlScreenTopBar(
                isVisible = state.visibleBars,
                title = url,
                onBackClicked = {
                    navController.navigate(Screen.ImagesScreen.route) {
                        popUpTo(Screen.SharedUrlScreen.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()) {
            var imageSize by remember {
                mutableStateOf<Size?>(null)
            }
            val overZoomConfig = OverZoomConfig(
                minSnapScale = 1f,
                maxSnapScale = 1.7f
            )
            val zoomableState = rememberZoomableState(
                initialScale = 1f,
                minScale = 0.1f,
                overZoomConfig = overZoomConfig,
            )
            val isHorizontalOrientation = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            Zoomable(
                state = zoomableState,
                onTap = {
                    viewModel.onBarsVisibilityChange()
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
                    model = url,
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.image_not_found),
                    placeholder = if(isSuccess)
                        painterResource(id = R.drawable.placeholder) else {
                        painterResource(R.drawable.image_not_found)
                    },
                    onSuccess = { painterState ->
                        isSuccess = true
                        imageSize = painterState.painter.intrinsicSize
                        isLoading = false
                    },
                    onError = {
                        isSuccess = false
                        isLoading = false
                    },
                    contentDescription = null,
                )
            }
        }
    }
}