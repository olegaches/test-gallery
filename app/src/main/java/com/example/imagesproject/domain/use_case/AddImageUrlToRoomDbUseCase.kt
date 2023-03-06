package com.example.imagesproject.domain.use_case

import com.example.imagesproject.data.local.ImageUrlDao
import com.example.imagesproject.data.local.entity.ImageUrlEntity
import javax.inject.Inject

class AddImageUrlToRoomDbUseCase @Inject constructor(
    private val imageUrlDao: ImageUrlDao
) {
    suspend operator fun invoke(imageUrl: String) =
        imageUrlDao.insertImageUrl(ImageUrlEntity(imageUrl = imageUrl))
}