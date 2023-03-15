package com.example.imagesproject.data.local.datastore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.imagesproject.core.util.Extension.isCompatibleWithApi28
import com.example.imagesproject.core.util.Extension.isPowerSavingMode
import com.example.imagesproject.domain.datastore.UserPreferences
import com.example.imagesproject.domain.model.AppConfiguration
import com.example.imagesproject.domain.type.ThemeStyleType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class UserPreferencesImplDataStore @Inject constructor(
    private val dataStorePreferences: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
) : UserPreferences {
    private val tag = this::class.java.simpleName
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = goAsync {
            if (intent.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                updateDataStoreFlow()
            }
        }
    }
    init {
        context.registerReceiver(broadcastReceiver, android.content.IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
    }

    override val appConfigurationStream: Flow<AppConfiguration> = dataStorePreferences.data
        .catch { exception ->
            exception.localizedMessage?.let { Log.e(tag, it) }
            emit(value = emptyPreferences())
        }
        .map { preferences ->
            val useDynamicColors = preferences[PreferencesKeys.useDynamicColors] ?: true
            val themeStyle = preferences[PreferencesKeys.themeStyle].toThemeStyleType()
            AppConfiguration(
                useDynamicColors = useDynamicColors,
                themeStyle = themeStyle,
                usePowerSavingMode = isPowerSavingMode(),
            )
        }

    private fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob()).launch(context) {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override suspend fun toggleDynamicColors() {
        tryIt {
            dataStorePreferences.edit { preferences ->
                val key = PreferencesKeys.useDynamicColors
                val current = preferences[key] ?: true
                preferences[key] = !current
            }
        }
    }

    private suspend fun updateDataStoreFlow() {
        tryIt {
            dataStorePreferences.edit { preferences ->
                val key = PreferencesKeys.updateFlag
                val current = preferences[key] ?: false
                preferences[key] = !current
            }
        }
    }

    override suspend fun changeThemeStyle(themeStyle: ThemeStyleType) {
        tryIt {
            dataStorePreferences.edit { prefs ->
                prefs[PreferencesKeys.themeStyle] = themeStyle.name
            }
        }
    }

    private suspend fun tryIt(action: suspend () -> Unit) {
        try {
            action()
        } catch (exception: Exception) {
            exception.localizedMessage?.let { Log.e(tag, it) }
        }
    }

    private fun String?.toThemeStyleType(): ThemeStyleType {
        val lightMode = ThemeStyleType.LightMode
        val darkMode = ThemeStyleType.DarkMode
        val followPowerSavingMode = ThemeStyleType.FollowPowerSavingMode
        return when (this) {
            lightMode.name -> lightMode
            darkMode.name -> darkMode
            followPowerSavingMode.name -> followPowerSavingMode
            else -> if (!isCompatibleWithApi28()) {
                followPowerSavingMode
            } else {
                ThemeStyleType.FollowAndroidSystem
            }
        }
    }

    private object PreferencesKeys {
        val useDynamicColors = booleanPreferencesKey(name = "use_dynamic_colors")
        val updateFlag = booleanPreferencesKey(name = "update_flag")
        val themeStyle = stringPreferencesKey(name = "theme_style")
    }
}