package com.example.imagesproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.example.imagesproject.core.util.Extension
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi26
import com.example.imagesproject.presentation.Constants
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ImagesApp: Application() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var powerManager: PowerManager
    override fun onCreate() {
        super.onCreate()
        Extension.init(
            powerManager = powerManager,
        )
        if(isCompatibleWithApi26()) {
            createNotification()
        }
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