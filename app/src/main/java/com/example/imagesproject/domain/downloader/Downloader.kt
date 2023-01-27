package com.example.imagesproject.domain.downloader

interface Downloader {
    fun downloadFile(url: String): Long
}