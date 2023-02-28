package com.example.imagesproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.imagesproject.presentation.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ImagesApp: Application() {
    @Inject lateinit var notificationManager: NotificationManager
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.e("666", "terminate")
        notificationManager.cancel(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        )
        notificationManager.createNotificationChannel(channel)
    }
}