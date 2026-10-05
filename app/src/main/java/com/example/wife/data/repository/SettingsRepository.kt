package com.example.wife.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val NICKNAME_KEY = stringPreferencesKey("nickname")
        val PERMISSION_MODE_KEY = stringPreferencesKey("permission_mode")
    }

    val nickname: Flow<String> = dataStore.data.map { preferences ->
        preferences[NICKNAME_KEY] ?: "Sayang"
    }

    val permissionMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[PERMISSION_MODE_KEY] ?: "STRICT"
    }

    suspend fun setNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME_KEY] = nickname
        }
    }

    suspend fun setPermissionMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PERMISSION_MODE_KEY] = mode
        }
    }
}
