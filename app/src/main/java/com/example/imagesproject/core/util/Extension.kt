package com.example.imagesproject.core.util

import android.os.Build
import android.os.PowerManager
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.imagesproject.domain.type.ThemeStyleType


/**
 * Check if this device is compatible with Dynamic Colors for Material 3.
 *
 * @return true when this device is API 31 (Android 12) or up, false otherwise.
 */

object Extension {
    private lateinit var powerManager: PowerManager

    fun init(
        powerManager: PowerManager,
    ) {
        this.powerManager = powerManager
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isCompatibleWithDynamicColors(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun isCompatibleWithApi28(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isCompatibleWithApi26(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    fun isCompatibleWithApi29(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun isCompatibleWithApi33(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

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
    ): Boolean {
        val powerSavingMode = if(!isCompatibleWithApi29()) {
            isPowerSavingMode()
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

    fun isPowerSavingMode(): Boolean {
        return try {
            powerManager.isPowerSaveMode
        } catch (e: Exception) {
            false
        }
    }
}