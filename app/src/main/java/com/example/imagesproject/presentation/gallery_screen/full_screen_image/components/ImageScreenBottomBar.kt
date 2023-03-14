package com.example.imagesproject.presentation.gallery_screen.full_screen_image.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Parcelable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.imagesproject.core.util.Extension.trySystemAction
import com.example.imagesproject.presentation.Constants


private fun getIntentChooser(context: Context, intent: Intent, chooserTitle: CharSequence? = null): Intent? {
    var resolveInfos: MutableList<ResolveInfo> = mutableListOf()
    trySystemAction {
        resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
    }
    val excludedComponentNames = HashSet<ComponentName>()
    val targetIntents: MutableList<Intent> = ArrayList()
    resolveInfos.forEach {
        val activityInfo = it.activityInfo
        val componentName = ComponentName(activityInfo.packageName, activityInfo.name)
        if(activityInfo.packageName == context.packageName) {
            excludedComponentNames.add(componentName)
        } else {
            val targetIntent = Intent(intent)
            targetIntent.setPackage(activityInfo.packageName)
            targetIntent.component = componentName
            // wrap with LabeledIntent to show correct name and icon
            val labeledIntent = LabeledIntent(
                targetIntent,
                activityInfo.packageName,
                it.labelRes,
                it.icon
            )
            // add filtered intent to a list
            targetIntents.add(labeledIntent)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Intent.createChooser(intent, chooserTitle)
            .putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames.toTypedArray())
    } else {
        // deal with M list seperate problem
        val chooserIntent: Intent = Intent.createChooser(Intent(), chooserTitle) ?: return null
        // add initial intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            targetIntents.toTypedArray<Parcelable>()
        )
        return chooserIntent
    }
}

@Composable
fun ImageScreenBottomBar(imageUrl: String, isVisible: Boolean, onErrorOccurred: () -> Unit) {
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
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val context = LocalContext.current
                val intent = remember {
                    val targetIntent = Intent(Intent.ACTION_SEND)
                    targetIntent.type = "text/plain"
                    targetIntent.putExtra(Intent.EXTRA_SUBJECT, "subject")
                    targetIntent.putExtra(Intent.EXTRA_TEXT, imageUrl)
                }
                IconButton(
                    onClick = {
                        val chooserIntent = getIntentChooser(context, intent.putExtra(Intent.EXTRA_TEXT, imageUrl))
                        if(!trySystemAction {
                            context.startActivity(chooserIntent)
                        }) {
                            onErrorOccurred()
                        }
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