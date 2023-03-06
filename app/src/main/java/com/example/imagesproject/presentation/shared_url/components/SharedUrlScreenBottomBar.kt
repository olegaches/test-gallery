package com.example.imagesproject.presentation.shared_url.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.imagesproject.R
import com.example.imagesproject.presentation.Constants

@Composable
fun SharedUrlScreenBottomBar(
    isVisible: Boolean,
    isSuccess: Boolean,
    onSaveImage: () -> Unit,
    onCancel: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(Constants.TOP_BAR_VISIBILITY_ENTRY_ANIMATION_TIME)
        ),
        exit = fadeOut(
            animationSpec = tween(Constants.TOP_BAR_VISIBILITY_EXIT_ANIMATION_TIME)
        )
    ) {
        BottomAppBar(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier
                    //.padding(horizontal = 6.dp)
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if(isSuccess) {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp).weight(1f),
                        onClick = onSaveImage
                    ) {
                        Text(
                            text = stringResource(R.string.save_image_button_text)
                        )
                    }
                }
                OutlinedButton(
                    modifier = Modifier.padding(horizontal = 10.dp).weight(1f),
                    onClick = onCancel
                ) {
                    Text(
                        text = stringResource(R.string.cancel_saving_image_button_text),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}