package com.example.imagesproject.presentation.images_screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Screen
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.*
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
    navController: NavController,
    viewModel: ImagesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
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
                .padding(paddingValues)
                .fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(100.dp),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(0.dp),
            ) {
                items(state.imagesList.size) { index ->
                    val imageUrl = state.imagesList[index]
                    GlideImage(
                        imageModel = { imageUrl },
                        modifier = Modifier
                            .size(130.dp)
                            .clickable {
                                val encodedUrl =
                                    URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                                navController.navigate(Screen.ImageItemScreen.withArgs(encodedUrl))
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
}