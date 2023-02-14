package com.example.imagesproject.di

import com.example.imagesproject.core.util.CoroutinesDispatchers
import com.example.imagesproject.core.util.CoroutinesDispatchersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoroutinesModule {
    @Binds
    @Singleton
    fun bindCoroutinesDispatchers(
        dispatchers: CoroutinesDispatchersImpl
    ): CoroutinesDispatchers
}