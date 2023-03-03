package com.example.imagesproject.di

import com.example.imagesproject.data.file_provider.FileProviderImpl
import com.example.imagesproject.data.repository.RepositoryImpl
import com.example.imagesproject.domain.file_provider.FileProvider
import com.example.imagesproject.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: RepositoryImpl
    ): Repository

    @Binds
    @Singleton
    abstract fun bindFileProvider(
        fileProviderImpl: FileProviderImpl
    ): FileProvider
}