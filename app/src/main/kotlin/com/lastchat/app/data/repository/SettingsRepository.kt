package com.lastchat.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        val KEY_DEFAULT_PROVIDER = stringPreferencesKey("default_provider_id")
        val KEY_STREAMING_ENABLED = booleanPreferencesKey("streaming_enabled")
        val KEY_CONTEXT_LENGTH = stringPreferencesKey("context_length")
    }

    val dynamicColor: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DYNAMIC_COLOR] != false
    }

    val darkMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: "system"
    }

    val streamingEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_STREAMING_ENABLED] != false
    }

    val contextLength: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_CONTEXT_LENGTH]?.toIntOrNull() ?: 10
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setDarkMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = mode
        }
    }

    suspend fun setStreamingEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_STREAMING_ENABLED] = enabled
        }
    }

    suspend fun setContextLength(length: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_CONTEXT_LENGTH] = length.toString()
        }
    }

    suspend fun setDefaultProvider(providerId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_PROVIDER] = providerId
        }
    }

    fun getDefaultProvider(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_PROVIDER]
    }
}
