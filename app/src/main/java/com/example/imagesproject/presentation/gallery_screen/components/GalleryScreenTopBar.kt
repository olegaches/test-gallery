package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.imagesproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
    )
}