package com.example.imagesproject.domain.service

interface ImageService {
    fun showNotification(url: String)

    fun hideNotification()
}