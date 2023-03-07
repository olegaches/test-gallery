package com.example.imagesproject.presentation.gallery_screen.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.imagesproject.presentation.Constants


private fun getIntentChooser(context: Context, intent: Intent, chooserTitle: CharSequence? = null): Intent? {
    val resolveInfos = context.packageManager.queryIntentActivities(intent, 0)
    val excludedComponentNames = HashSet<ComponentName>()
    resolveInfos.forEach {
        val activityInfo = it.activityInfo
        if(activityInfo.packageName == context.packageName) {
            excludedComponentNames.add(ComponentName(activityInfo.packageName, activityInfo.name))
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Intent.createChooser(intent, chooserTitle)
            .putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames.toTypedArray())
    }
    if (resolveInfos.isNotEmpty()) {
        val targetIntents: MutableList<Intent> = ArrayList()
        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo
            if (excludedComponentNames.contains(
                    ComponentName(
                        activityInfo.packageName,
                        activityInfo.name
                    )
                )
            )
                continue
            val targetIntent = Intent(intent)
            targetIntent.setPackage(activityInfo.packageName)
            targetIntent.component = ComponentName(activityInfo.packageName, activityInfo.name)
            // wrap with LabeledIntent to show correct name and icon
            val labeledIntent = LabeledIntent(
                targetIntent,
                activityInfo.packageName,
                resolveInfo.labelRes,
                resolveInfo.icon
            )
            // add filtered intent to a list
            targetIntents.add(labeledIntent)
        }
        // deal with M list seperate problem
        val chooserIntent: Intent = Intent.createChooser(Intent(), chooserTitle) ?: return null
        // add initial intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            targetIntents.toTypedArray<Parcelable>()
        )
        return chooserIntent
    }
    return null
}

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
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val context = LocalContext.current
                val targetIntent = Intent(Intent.ACTION_SEND)
                targetIntent.type = "text/plain"
                targetIntent.putExtra(Intent.EXTRA_SUBJECT, "subject")
                targetIntent.putExtra(Intent.EXTRA_TEXT, imageUrl)
                val intent = getIntentChooser(context, targetIntent)

                IconButton(
                    onClick = {
                        context.startActivity(intent)
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