package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TransparentSystemBars(isDarkTheme: Boolean) {
    val transparentColor = Color.Transparent
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        systemUiController.setSystemBarsColor(transparentColor)
        systemUiController.setNavigationBarColor(
            darkIcons = !isDarkTheme,
            color = transparentColor,
            navigationBarContrastEnforced = false,
        )
        systemUiController.setStatusBarColor(
            color = transparentColor,
            darkIcons = !isDarkTheme
        )
    }
}