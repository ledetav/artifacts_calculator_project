package com.nokaori.genshinaibuilder.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeManager(context: Context) {
    private val dataStore = context.dataStore

    companion object{
        val IS_DARKMODE_KEY = booleanPreferencesKey("is_darkmode")
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARKMODE_KEY] ?: false
    }

    suspend fun setTheme(isDark: Boolean) {
        dataStore.edit { settings ->
            settings[IS_DARKMODE_KEY] = isDark
        }
    }
}