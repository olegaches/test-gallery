package com.example.imagesproject.data.downloader

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.example.imagesproject.domain.downloader.Downloader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class DownloaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
): Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val file = File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/images.txt")
        if(file.exists()) {
            file.delete()
        }
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("text/plain")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, "images.txt")
        return downloadManager.enqueue(request)
    }
}