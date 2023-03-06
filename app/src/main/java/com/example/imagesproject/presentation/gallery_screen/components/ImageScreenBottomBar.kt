package com.example.imagesproject.presentation.gallery_screen.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.imagesproject.presentation.Constants

@Composable
fun ImageScreenBottomBar(imageUrl: String, isVisible: Boolean) {
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
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, imageUrl)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                val context = LocalContext.current
                IconButton(
                    onClick = {
                        context.startActivity(shareIntent)
                    }
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Filled.Share,
                        contentDescription = null
                    )
                }
            }
        }
    }
}