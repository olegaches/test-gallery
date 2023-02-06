package com.example.imagesproject.data.repository

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.downloader.Downloader
import com.example.imagesproject.domain.file_provider.FileProvider
import com.example.imagesproject.domain.repository.Repository
import com.example.imagesproject.presentation.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val fileProvider: FileProvider,
    private val downloader: Downloader,
    @ApplicationContext private val context: Context,
): Repository {
    override fun getImagesUrlList(): Flow<Resource<List<String>>> {
        return callbackFlow {
            downloader.downloadFile(Constants.FILE_URL)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
                        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                        if (id != -1L) {
                            val urlList = mutableListOf<String>()
                            val file = fileProvider.getFile()
                            val lines = file?.readLines()
                            if (lines != null) {
                                for(line in lines) {
                                    urlList.add(line)
                                }
                            }
                            trySend(Resource.Success(urlList.toList()))
                        }
                    }
                }
            }
            context.registerReceiver(receiver,
                android.content.IntentFilter("android.intent.action.DOWNLOAD_COMPLETE")
            )

            awaitClose {
                context.unregisterReceiver(receiver)
            }

        }.flowOn(Dispatchers.IO)
    }
}