package com.example.imagesproject.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.imagesproject.R
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi23
import com.example.imagesproject.core.util.Extension.trySystemAction
import com.example.imagesproject.domain.service.ImageService
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class ImageServiceImpl @Inject constructor(): ImageService, Service() {
    @Inject lateinit var notificationManager: NotificationManager
    @Inject @ApplicationContext lateinit var context: Context

    private var currentNotification: Notification? = null

    private var currentUrl: String? = null

    override fun getCurrentUrl(): String? {
        return currentUrl
    }

    override fun getCurrentNotification(): Notification? {
        return currentNotification
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val url = intent.getStringExtra(Constants.SERVICE_PARAM_IMAGE_URL)
        val currentApplicationContext = applicationContext
        val resultIntent = Intent(currentApplicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val resultPendingIntent = if(isCompatibleWithApi23()) {
            PendingIntent.getActivity(
                currentApplicationContext, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                currentApplicationContext, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE
            )
        }
        val notification = NotificationCompat.Builder(
            currentApplicationContext,
            Constants.NOTIFICATION_CHANNEL_ID
        )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setContentText(url.orEmpty())
            .setSmallIcon(R.drawable.ic_android_24)
            .setContentIntent(resultPendingIntent)
            .build()
        currentNotification = notification
        trySystemAction {
            notificationManager.notify(1, notification)
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        trySystemAction {
            val context = context
            notificationManager.cancel(1)
            context.stopService(Intent(context, ImageServiceImpl::class.java))
        }
        currentNotification = null
        super.onTaskRemoved(rootIntent)
    }

    override fun showNotification(url: String) {
        currentUrl = url
        val context = context
        context.startService(Intent(context, ImageServiceImpl::class.java).putExtra(Constants.SERVICE_PARAM_IMAGE_URL, url))
    }

    override fun hideNotification() {
        val context = context
        trySystemAction {
            notificationManager.cancel(1)
            context.stopService(Intent(context, ImageServiceImpl::class.java))
        }
        currentUrl = null
        currentNotification = null
    }
}