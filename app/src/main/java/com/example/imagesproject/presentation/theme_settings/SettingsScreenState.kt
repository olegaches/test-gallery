package com.example.imagesproject.presentation.theme_settings

import com.example.imagesproject.domain.type.ThemeStyleType

data class ThemeSettingsScreenState(
    val useDynamicColors: Boolean,
    val themeStyle: ThemeStyleType
)