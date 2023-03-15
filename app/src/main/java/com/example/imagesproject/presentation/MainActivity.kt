package com.example.imagesproject.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi33
import com.example.imagesproject.core.util.Extension.shouldUseDarkTheme
import com.example.imagesproject.domain.type.Screen
import com.example.imagesproject.presentation.gallery_screen.images_list.GalleryScreen
import com.example.imagesproject.presentation.shared_url.SharedUrlScreen
import com.example.imagesproject.presentation.theme_settings.ThemeSettingsScreen
import com.example.imagesproject.ui.theme.ImagesProjectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var navHostController: NavHostController

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.action == Intent.ACTION_SEND) {
            viewModel.cancelNotification()
        }
        navHostController.handleDeepLink(intent = intent)
    }

    private fun wasLaunchedFromRecents(intent: Intent): Boolean {
        return intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onCreate", "onDestroy")
    }

    override fun onStop() {
        super.onStop()
        viewModel.delayCancelNotification()
    }

    override fun onResume() {
        super.onResume()
        viewModel.recreateNotification()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.delayCancelNotification()
        Log.e("onCreate", "onCreateMain")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val activityState by viewModel.activityState.collectAsState()
            navHostController = rememberNavController()
            if(isCompatibleWithApi33()) {
                val notificationPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
                if(!notificationPermissionState.status.isGranted) {
                    SideEffect {
                        notificationPermissionState.launchPermissionRequest()
                    }
                }
            }
            TransparentSystemBars(shouldUseDarkTheme(themeStyle = activityState.themeStyle))
            when (activityState.isLoading) {
                true -> ImagesProjectTheme {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                false -> ImagesProjectTheme(
                    darkTheme = shouldUseDarkTheme(themeStyle = activityState.themeStyle),
                    dynamicColor = activityState.useDynamicColors
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigation(navHostController, wasLaunchedFromRecents(intent = intent))
                    }
                }
            }
        }
    }
}

@Composable
fun TransparentSystemBars(isDarkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    val transparentColor = Color.Transparent

    SideEffect {
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

@Composable
fun Navigation(navHostController: NavHostController, isLaunchedFromRecents: Boolean) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.ImagesScreen.route
    ) {
        composable(
            route = Screen.ImagesScreen.route
        ) {
            GalleryScreen(
                navController = navHostController
            )
        }
        composable(
            route = Screen.ThemeSettingsScreen.route
        ) {
            ThemeSettingsScreen(
                navController = navHostController
            )
        }
        if(!isLaunchedFromRecents) {
            composable(
                route = Screen.SharedUrlScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        action = Intent.ACTION_SEND
                        mimeType = "text/*"
                    }
                )
            ) {
                SharedUrlScreen(navController = navHostController)
            }
        }
    }
}