package com.example.imagesproject.data.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.example.imagesproject.domain.downloader.Downloader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
): Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("text/plain")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("images.txt")
            .addRequestHeader("Authorization", "Bearer <token>")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "images.txt")
        return downloadManager.enqueue(request)
    }
}