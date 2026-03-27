package com.nokaori.genshinaibuilder.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
        val LAST_SYNC_TIME_KEY = longPreferencesKey("last_sync_time")
    }

    override val appLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_LANGUAGE_KEY] ?: getSystemLanguage()
    }

    override suspend fun setAppLanguage(language: String) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.APP_LANGUAGE_KEY] = language
        }
    }

    override val lastSyncTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_SYNC_TIME_KEY] ?: 0L
    }

    override suspend fun setLastSyncTime(time: Long) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.LAST_SYNC_TIME_KEY] = time
        }
    }

    private fun getSystemLanguage(): String {
        val systemLang = Locale.getDefault().language
        return if (systemLang == "ru") SupportedLanguages.RU else SupportedLanguages.EN
    }
}