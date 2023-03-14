package com.example.imagesproject.presentation.gallery_screen.full_screen_image.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.imagesproject.R

@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButtonClick: () -> Unit,
) {
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
                    text = stringResource(R.string.delete_button_text)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(R.string.cancel_button_text)
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