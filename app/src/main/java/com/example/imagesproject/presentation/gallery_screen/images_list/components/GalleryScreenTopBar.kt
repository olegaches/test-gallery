package com.example.imagesproject.presentation.gallery_screen.images_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.imagesproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreenTopBar(
    onThemeSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title),
            )
        },
        actions = {
            Column {
                var menuVisible by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    onClick = {
                        menuVisible = !menuVisible
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                    )
                }
                DropdownMenu(
                    expanded = menuVisible,
                    onDismissRequest = {
                        menuVisible = false
                    }
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxSize()
                        ,
                        text = {
                            Text(
                                text = stringResource(R.string.theme_label_text)
                            )
                        },
                        onClick = onThemeSettingsClick
                    )
                }
            }
        }
    )
}