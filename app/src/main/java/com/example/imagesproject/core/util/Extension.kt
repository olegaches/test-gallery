package com.example.imagesproject.core.util

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.getSystemService
import com.example.imagesproject.domain.type.ThemeStyleType


/**
 * Check if this device is compatible with Dynamic Colors for Material 3.
 *
 * @return true when this device is API 31 (Android 12) or up, false otherwise.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isCompatibleWithDynamicColors(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isCompatibleWithApi28(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isCompatibleWithApi29(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

/**
 * Map a ThemeStyleType into a [Boolean].
 *
 * @param themeStyle the [ThemeStyleType].
 *
 * @return the corresponding boolean value of this ThemeStyleType.
 */
@Composable
fun shouldUseDarkTheme(
    themeStyle: ThemeStyleType,
    context: Context
): Boolean {
    val powerSavingMode = if(!isCompatibleWithApi29()) {
        isPowerSavingMode(context)
    } else {
        isSystemInDarkTheme()
    }
    return when (themeStyle) {
        ThemeStyleType.FollowAndroidSystem -> isSystemInDarkTheme()
        ThemeStyleType.LightMode -> false
        ThemeStyleType.DarkMode -> true
        ThemeStyleType.FollowPowerSavingMode -> powerSavingMode
    }
}

fun isPowerSavingMode(context: Context): Boolean {
    val powerManager = getSystemService(context, PowerManager::class.java) as PowerManager
    return powerManager.isPowerSaveMode
}