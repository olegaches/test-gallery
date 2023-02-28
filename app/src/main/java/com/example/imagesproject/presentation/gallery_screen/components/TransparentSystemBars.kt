package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TransparentSystemBars(isDarkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        systemUiController.setSystemBarsColor(Color.Transparent)
        systemUiController.setNavigationBarColor(
            darkIcons = !isDarkTheme,
            color = Color.Transparent,
            navigationBarContrastEnforced = false,
        )
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme
        )
    }
}