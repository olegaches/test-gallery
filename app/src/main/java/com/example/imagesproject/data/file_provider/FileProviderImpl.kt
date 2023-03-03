package com.example.imagesproject.data.file_provider

import android.content.Context
import com.example.imagesproject.domain.file_provider.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): FileProvider {
    override fun getFile(): File {
        return File("${context.filesDir}/images")
    }
}