package com.example.imagesproject.presentation.gallery_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.imagesproject.R
import com.example.imagesproject.core.util.UiText

@Composable
fun ErrorLabel(
    error: UiText,
    modifier: Modifier,
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error.asString(),
            textAlign = TextAlign.Center,
        )
        TextButton(
            onClick = onRefreshClick,
        ) {
            Text(
                text = stringResource(R.string.refresh_page_button_text),
            )
        }
    }
}