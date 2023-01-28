package com.example.imagesproject.presentation.images_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
    navController: NavController,
    viewModel: ImagesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
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
                .padding(paddingValues)
                .fillMaxSize()) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(90.dp),
            ) {
                items(state.imagesList.size) { index ->
                    val imageUrl = state.imagesList[index]
                    GlideImage(
                        imageModel = { imageUrl },
                        modifier = Modifier
                            .padding(1.dp)
                            .size(100.dp)
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