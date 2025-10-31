package com.nokaori.genshinaibuilder.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val IS_DARKMODE_KEY = booleanPreferencesKey("is_darkmode")
    }

    override val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARKMODE_KEY] ?: false
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.IS_DARKMODE_KEY] = isDark
        }
    }
}