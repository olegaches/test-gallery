package com.example.imagesproject.presentation.gallery_screen.full_screen_image.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.imagesproject.R

@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButtonClick: () -> Unit,
) {
    val mediumFontWeight = remember { FontWeight.Medium }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.delete_dialog_title)
            )
        },
        confirmButton = {
            TextButton(
                onClick = confirmButtonClick,
            ) {
                Text(
                    color = MaterialTheme.colorScheme.error,
                    text = stringResource(R.string.delete_button_text),
                    fontWeight = mediumFontWeight,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(R.string.cancel_button_text),
                    fontWeight = mediumFontWeight,
                )
            }
        },
        text = {
            Text(
                text = stringResource(R.string.delete_dialog_text),
            )
        }
    )
}