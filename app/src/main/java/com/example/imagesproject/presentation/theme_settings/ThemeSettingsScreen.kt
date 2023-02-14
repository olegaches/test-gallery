package com.example.imagesproject.presentation.theme_settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.imagesproject.R
import com.example.imagesproject.presentation.theme_settings.components.ThemeStyleSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    navController: NavController,
    viewModel: ThemeSettingsViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.theme_settings_title_text)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                ,
        ) {
            ThemeStyleSection(
                modifier = Modifier.padding(horizontal = 32.dp),
                themeStyle = screenState.themeStyle,
                changeThemeStyle = { viewModel.changeThemeStyle(themeStyle = it) }
            )
        }
    }
}