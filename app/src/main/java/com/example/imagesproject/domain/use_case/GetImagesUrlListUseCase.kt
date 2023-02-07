package com.example.imagesproject.domain.use_case

import com.example.imagesproject.core.util.Resource
import com.example.imagesproject.domain.model.ImageItem
import com.example.imagesproject.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImagesUrlListUseCase @Inject constructor(
    private val repository: Repository,
) {
    operator fun invoke(): Flow<Resource<List<ImageItem>>> {
        return repository.getImagesUrlList()
    }
}