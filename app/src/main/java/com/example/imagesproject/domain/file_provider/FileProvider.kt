package com.example.imagesproject.domain.file_provider

import java.io.File

interface FileProvider {
    fun getFile(): File
}