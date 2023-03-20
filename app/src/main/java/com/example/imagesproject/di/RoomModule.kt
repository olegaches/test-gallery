package com.example.imagesproject.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.imagesproject.data.local.AppDatabase
import com.example.imagesproject.data.local.ImageUrlDao
import com.example.imagesproject.presentation.Constants.IMAGES_URL_TABLE_NAME
import com.example.imagesproject.presentation.Constants.IMAGES_URL_TABLE_NAME_NEW
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE ${Constants.IMAGES_URL_TABLE_NAME} ADD COLUMN id INTEGER")
//            database.execSQL("ALTER TABLE ${Constants.IMAGES_URL_TABLE_NAME} ADD COLUMN id INTEGER")
            // Remove first and last name column from profile
            // create new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `$IMAGES_URL_TABLE_NAME_NEW` (`imageUrl` TEXT NOT NULL, `id` INTEGER PRIMARY KEY)")
            // create nickname if needed
            database.execSQL("UPDATE `$IMAGES_URL_TABLE_NAME` SET `imageUrl` = `imageUrl` || ''")
            // copy data to new table
            database.execSQL("INSERT INTO `$IMAGES_URL_TABLE_NAME_NEW` (`imageUrl`) SELECT `imageUrl` FROM `$IMAGES_URL_TABLE_NAME`")
            // remove the old table
            database.execSQL("DROP TABLE `$IMAGES_URL_TABLE_NAME`")
            // rename new table
            database.execSQL("ALTER TABLE `$IMAGES_URL_TABLE_NAME_NEW` RENAME TO `$IMAGES_URL_TABLE_NAME`")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // create new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `$IMAGES_URL_TABLE_NAME_NEW` (`imageUrl` TEXT NOT NULL, `id` INTEGER PRIMARY KEY, `location` TEXT)")
            // copy data to new table
            database.execSQL("INSERT INTO `$IMAGES_URL_TABLE_NAME_NEW` (`imageUrl`, `location`) SELECT `imageUrl`, null FROM `$IMAGES_URL_TABLE_NAME`")
            // remove the old table
            database.execSQL("DROP TABLE `$IMAGES_URL_TABLE_NAME`")
            // rename new table
            database.execSQL("ALTER TABLE `$IMAGES_URL_TABLE_NAME_NEW` RENAME TO `$IMAGES_URL_TABLE_NAME`")
        }
    }
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app, AppDatabase::class.java, AppDatabase.name
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageUrlDao(
        db: AppDatabase,
    ): ImageUrlDao {
        return db.imageUrlDao
    }
}