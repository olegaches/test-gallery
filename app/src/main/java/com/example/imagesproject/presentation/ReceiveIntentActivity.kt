package com.example.imagesproject.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.imagesproject.core.util.Extension.trySystemAction

class ReceiveIntentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launchIntent = Intent(this.applicationContext, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if(intent.action == Intent.ACTION_SEND) {
            when(intent.type) {
                "text/plain" -> {
                    val resultIntent = Intent(applicationContext, MainActivity::class.java)
                        .setAction(Intent.ACTION_SEND)
                    resultIntent.type = "text/plain"
                    resultIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        intent.getStringExtra(Intent.EXTRA_TEXT)
                    )
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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