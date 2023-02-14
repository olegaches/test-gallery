package com.example.imagesproject.domain.use_case

import com.example.imagesproject.domain.datastore.UserPreferences
import com.example.imagesproject.domain.model.AppConfiguration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppConfigurationStreamUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    operator fun invoke(): Flow<AppConfiguration> =
        userPreferences.appConfigurationStream
}