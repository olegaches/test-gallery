package com.example.imagesproject.presentation.theme_settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.imagesproject.R
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi28
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi29
import com.example.imagesproject.domain.type.ThemeStyleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStyleSection(
    modifier: Modifier = Modifier,
    themeStyle: ThemeStyleType,
    changeThemeStyle: (ThemeStyleType) -> Unit
) = Column(modifier = modifier) {
    val iconSize = remember { AssistChipDefaults.IconSize }
    if(isCompatibleWithApi28()) {
        val followAndroidSystemTheme = remember { ThemeStyleType.FollowAndroidSystem }
        InputChip(
            selected = themeStyle == followAndroidSystemTheme,
            onClick = {
                if (themeStyle != followAndroidSystemTheme)
                    changeThemeStyle(followAndroidSystemTheme)
            },
            label = { Text(text = stringResource(R.string.android_theme_text)) },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(size = iconSize),
                    painter = painterResource(id = R.drawable.ic_android_24),
                    contentDescription = null
                )
            }
        )
    }
    if(!isCompatibleWithApi29()) {
        val followPowerSavingMode = remember { ThemeStyleType.FollowPowerSavingMode }
        InputChip(
            selected = themeStyle == followPowerSavingMode,
            onClick = {
                if (themeStyle != followPowerSavingMode)
                    changeThemeStyle(followPowerSavingMode)
            },
            label = { Text(text = stringResource(R.string.power_saving_mode_theme)) },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(size = iconSize),
                    painter = painterResource(id = R.drawable.ic_power_saving_24),
                    contentDescription = null
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
    val lightMode = remember { ThemeStyleType.LightMode }
    InputChip(
        selected = themeStyle == lightMode,
        onClick = {
            if (themeStyle != lightMode)
                changeThemeStyle(lightMode)
        },
        label = { Text(text = stringResource(R.string.light_theme_text)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(size = iconSize),
                painter = painterResource(id = R.drawable.outline_light_mode_24),
                contentDescription = null
            )
        }
    )

    val darkMode = remember { ThemeStyleType.DarkMode }
    InputChip(
        selected = themeStyle == darkMode,
        onClick = {
            if (themeStyle != darkMode)
                changeThemeStyle(darkMode)
        },
        label = { Text(text = stringResource(R.string.dark_theme_text)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(size = iconSize),
                painter = painterResource(id = R.drawable.outline_dark_mode_24),
                contentDescription = null
            )
        }
    )
}