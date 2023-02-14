package com.example.imagesproject.domain.model

import com.example.imagesproject.domain.type.ThemeStyleType


data class AppConfiguration(
    val useDynamicColors: Boolean,
    val themeStyle: ThemeStyleType
)