package com.example.imagesproject.domain.service

import android.app.Notification

interface ImageService {
    fun showNotification(url: String)

    fun hideNotification()

    fun getCurrentUrl(): String?

    fun getCurrentNotification(): Notification?
}