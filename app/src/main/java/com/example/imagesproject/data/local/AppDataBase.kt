package com.example.imagesproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.imagesproject.data.local.entity.ImageUrlEntity

@Database(
    entities = [ImageUrlEntity::class],
    version = 3
)

abstract class AppDatabase: RoomDatabase() {

    abstract val imageUrlDao: ImageUrlDao

    companion object {
        const val name = "app_dp"
    }
}