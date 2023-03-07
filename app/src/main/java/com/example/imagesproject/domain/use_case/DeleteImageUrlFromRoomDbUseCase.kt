package com.example.imagesproject.domain.use_case

import com.example.imagesproject.data.local.ImageUrlDao
import javax.inject.Inject

class DeleteImageUrlFromRoomDbUseCase @Inject constructor(
    private val imageUrlDao: ImageUrlDao
) {
    suspend operator fun invoke(imageUrl: String) {
        imageUrlDao.deleteImageUrl(imageUrl)
    }
}