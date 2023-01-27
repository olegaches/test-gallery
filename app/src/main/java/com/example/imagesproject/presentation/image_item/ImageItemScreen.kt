package com.example.imagesproject.presentation.image_item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Screen
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItemScreen(
    navController: NavController,
    viewModel: ImageItemVewModel = hiltViewModel(),
) {
    Scaffold(
    ) { padding ->
        GlideImage(
            imageModel = { viewModel.imageUrl },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit,
            ),
            component = rememberImageComponent {
                +ShimmerPlugin(
                    baseColor = MaterialTheme.colorScheme.background,
                    highlightColor = Color.LightGray,
                )
                +PalettePlugin(
                    imageModel = { viewModel.imageUrl },
                    useCache = true,
                )
            },
            // shows an error text if fail to load an image.
            failure = { Text(
                text = "image request failed.",
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
            ) },
        )
    }
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(R.string.top_bar_title),
//                        style = MaterialTheme.typography.titleMedium,
//                    )
//                },
//                navigationIcon = {
//                    navController.navigateUp()
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//            GlideImage(
//                imageModel = { viewModel.imageUrl },
//                modifier = Modifier
//                    .fillMaxSize(),
//                imageOptions = ImageOptions(
//                    contentScale = ContentScale.Crop,
//                ),
//                component = rememberImageComponent {
//                    +ShimmerPlugin(
//                        baseColor = MaterialTheme.colorScheme.background,
//                        highlightColor = Color.LightGray,
//                    )
//                    +PalettePlugin(
//                        imageModel = { viewModel.imageUrl },
//                        useCache = true,
//                    )
//                },
//                // shows an error text if fail to load an image.
//                failure = { Text(
//                    text = "image request failed.",
//                    modifier = Modifier.align(Alignment.Center),
//                    textAlign = TextAlign.Center,
//                ) },
//            )
//        }
//    }
}