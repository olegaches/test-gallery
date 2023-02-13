package com.example.imagesproject.di

import android.content.Context
import androidx.datastore.dataStore
import com.example.imagesproject.domain.model.ThemeEnum
import com.example.imagesproject.presentation.Constants
import com.example.imagesproject.presentation.UserSettingsSerializer
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore by dataStore(Constants.USER_SETTINGS_FILE_NAME, UserSettingsSerializer)

    @Singleton
    class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
        private val userSettingsDataStore = appContext.dataStore
        suspend fun setUserTheme(theme: ThemeEnum) {
            userSettingsDataStore.updateData {
                it.copy(theme = theme)
            }
        }

        val userSettings = userSettingsDataStore.data
    }
}