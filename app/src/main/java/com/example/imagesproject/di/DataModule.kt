package com.example.imagesproject.di

import com.example.imagesproject.data.datastore.UserPreferencesImplDataStore
import com.example.imagesproject.domain.datastore.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindUserPreferences(
        userPreferences: UserPreferencesImplDataStore
    ): UserPreferences
}