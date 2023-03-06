package com.example.imagesproject.di

import android.app.Application
import androidx.room.Room
import com.example.imagesproject.data.local.AppDatabase
import com.example.imagesproject.data.local.ImageUrlDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app, AppDatabase::class.java, AppDatabase.name
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageUrlDao(
        db: AppDatabase,
    ): ImageUrlDao {
        return db.imageUrlDao
    }
}