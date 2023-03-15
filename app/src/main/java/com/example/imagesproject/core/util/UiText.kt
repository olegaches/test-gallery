package com.example.imagesproject.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String): UiText {
        override fun asString(context: Context): String {
            return value
        }

        @Composable
        override fun asString(): String {
            return value
        }
    }

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText {
        override fun asString(context: Context): String {
            return context.getString(resId, *args)
        }

        @Composable
        override fun asString(): String {
            return stringResource(id = resId, *args)
        }
    }

    fun asString(context: Context): String
    @Composable
    fun asString(): String
}