package com.example.imagesproject.domain.repository

import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getImagesUrlList(): Flow<Resource<List<ImageItem>>>
}