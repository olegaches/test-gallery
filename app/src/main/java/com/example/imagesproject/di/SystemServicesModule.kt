package com.example.imagesproject.di

import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import android.os.PowerManager
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemServicesModule {
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager
    }

    @Provides
    @Singleton
    fun providePowerManager(
        @ApplicationContext context: Context,
    ): PowerManager {
        val powerManager = ContextCompat.getSystemService(
            context,
            PowerManager::class.java
        ) as PowerManager
        return powerManager
    }

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context,
    ): LocationManager {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager
    }

}