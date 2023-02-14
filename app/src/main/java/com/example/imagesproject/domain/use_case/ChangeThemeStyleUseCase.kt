package com.example.imagesproject.domain.use_case

import com.example.imagesproject.domain.datastore.UserPreferences
import com.example.imagesproject.domain.type.ThemeStyleType
import javax.inject.Inject
class ChangeThemeStyleUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(themeStyle: ThemeStyleType) =
        userPreferences.changeThemeStyle(themeStyle = themeStyle)
}