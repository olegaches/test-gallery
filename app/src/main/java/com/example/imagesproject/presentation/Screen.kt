package com.example.imagesproject.presentation

import com.example.imagesproject.R
import com.example.imagesproject.core.util.UiText

enum class Screen(val route: String, val screenName: UiText, val subRoutes: List<String>? = null) {
    ImagesScreen("ImagesScreen", UiText.StringResource(R.string.app_name)),
    ImageItemScreen("ImageItemScreen", UiText.StringResource(R.string.app_name)),
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