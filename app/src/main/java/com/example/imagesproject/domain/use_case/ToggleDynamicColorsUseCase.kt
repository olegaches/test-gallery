package com.example.imagesproject.domain.use_case

import com.example.imagesproject.domain.datastore.UserPreferences
import javax.inject.Inject

class ToggleDynamicColorsUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke() =
        userPreferences.toggleDynamicColors()
}