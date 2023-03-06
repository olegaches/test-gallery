package com.example.imagesproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imagesproject.data.local.entity.ImageUrlEntity
import com.example.imagesproject.presentation.Constants.IMAGES_URL_TABLE_NAME

@Dao
interface ImageUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageUrl(imageUrl: ImageUrlEntity)

    @Query("DELETE FROM $IMAGES_URL_TABLE_NAME WHERE imageUrl=:imageUrl")
    suspend fun deleteImageUrl(imageUrl: String)

    @Query("SELECT * FROM $IMAGES_URL_TABLE_NAME")
    suspend fun getImageUrlList(): List<ImageUrlEntity>

}