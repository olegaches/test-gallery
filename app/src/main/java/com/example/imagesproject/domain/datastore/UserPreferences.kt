package com.example.imagesproject.domain.datastore

import com.example.imagesproject.domain.model.AppConfiguration
import com.example.imagesproject.domain.type.ThemeStyleType
import kotlinx.coroutines.flow.Flow

interface UserPreferences {
    val appConfigurationStream: Flow<AppConfiguration>

    suspend fun toggleDynamicColors()

    suspend fun changeThemeStyle(themeStyle: ThemeStyleType)
}