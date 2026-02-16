package com.nokaori.genshinaibuilder.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : ThemeRepository {
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