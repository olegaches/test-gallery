package com.example.imagesproject.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.imagesproject.core.util.Extension.trySystemAction

class ReceiveIntentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationContext = applicationContext
        val intent = intent
        val classObj = MainActivity::class.java
        val clearTopFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val sendAction = Intent.ACTION_SEND
        val extraText = Intent.EXTRA_TEXT
        val launchIntent = Intent(applicationContext, classObj)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(clearTopFlag)
        if(intent.action == sendAction) {
            when(intent.type) {
                "text/plain" -> {
                    val resultIntent = Intent(applicationContext, classObj)
                        .setAction(sendAction)
                    resultIntent.type = "text/plain"
                    resultIntent.putExtra(
                        extraText,
                        intent.getStringExtra(extraText)
                    )
                    resultIntent.addFlags(clearTopFlag or Intent.FLAG_ACTIVITY_NEW_TASK)
                    trySystemAction {
                        startActivity(resultIntent)
                    }
                }
                else -> {
                    trySystemAction {
                        startActivity(launchIntent)
                    }
                }
            }
        } else {
            trySystemAction{
                startActivity(launchIntent)
            }
        }
    }

    override fun onDestroy() {
        Log.e("onDestroy", "onDestroy")
        super.onDestroy()
    }
}