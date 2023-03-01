package com.example.imagesproject.di

import com.example.imagesproject.data.service.ImageServiceImpl
import com.example.imagesproject.domain.service.ImageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {
    @Binds
    @Singleton
    fun provideImageService(
        imageServiceImpl: ImageServiceImpl,
    ): ImageService
}