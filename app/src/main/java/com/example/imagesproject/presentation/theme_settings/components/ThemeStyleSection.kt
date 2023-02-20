package com.example.imagesproject.presentation.theme_settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.imagesproject.R
import com.example.imagesproject.domain.type.ThemeStyleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStyleSection(
    modifier: Modifier = Modifier,
    themeStyle: ThemeStyleType,
    changeThemeStyle: (ThemeStyleType) -> Unit
) = Column(modifier = modifier) {
    InputChip(
        selected = themeStyle == ThemeStyleType.FollowAndroidSystem,
        onClick = {
            if (themeStyle != ThemeStyleType.FollowAndroidSystem)
                changeThemeStyle(ThemeStyleType.FollowAndroidSystem)
        },
        label = { Text(text = stringResource(R.string.android_theme_text)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                painter = painterResource(id = R.drawable.ic_android_24),
                contentDescription = null
            )
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    InputChip(
        selected = themeStyle == ThemeStyleType.LightMode,
        onClick = {
            if (themeStyle != ThemeStyleType.LightMode)
                changeThemeStyle(ThemeStyleType.LightMode)
        },
        label = { Text(text = stringResource(R.string.light_theme_text)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                painter = painterResource(id = R.drawable.outline_light_mode_24),
                contentDescription = null
            )
        }
    )

    InputChip(
        selected = themeStyle == ThemeStyleType.DarkMode,
        onClick = {
            if (themeStyle != ThemeStyleType.DarkMode)
                changeThemeStyle(ThemeStyleType.DarkMode)
        },
        label = { Text(text = stringResource(R.string.dark_theme_text)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                painter = painterResource(id = R.drawable.outline_dark_mode_24),
                contentDescription = null
            )
        }
    )
}