package com.example.imagesproject.domain.type

import com.example.imagesproject.R
import com.example.imagesproject.core.util.UiText

enum class Screen(val route: String, val screenName: UiText, val subRoutes: List<String>? = null) {
    ImagesScreen("ImagesScreen", UiText.StringResource(R.string.app_name)),
    ThemeSettingsScreen("ThemeSettingsScreen", UiText.StringResource(R.string.app_name)),
    ;

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}