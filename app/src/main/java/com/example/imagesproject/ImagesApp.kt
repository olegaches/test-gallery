package com.example.imagesproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ImagesApp: Application() {
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var notificationChannel: NotificationChannel
    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    private fun createNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}