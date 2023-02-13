package com.example.imagesproject.presentation

import com.example.imagesproject.domain.model.ThemeEnum
import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val theme: ThemeEnum = ThemeEnum.Auto,
)