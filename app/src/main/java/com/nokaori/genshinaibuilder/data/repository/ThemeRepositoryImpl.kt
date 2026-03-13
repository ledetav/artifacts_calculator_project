package com.nokaori.genshinaibuilder.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : ThemeRepository {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val IS_DARKMODE_KEY = booleanPreferencesKey("is_darkmode")
        val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    override val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARKMODE_KEY] ?: false
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.IS_DARKMODE_KEY] = isDark
        }
    }

    override val appLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_LANGUAGE_KEY] ?: getSystemLanguage()
    }

    override suspend fun setAppLanguage(language: String) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.APP_LANGUAGE_KEY] = language
        }
    }

    private fun getSystemLanguage(): String {
        val systemLang = Locale.getDefault().language
        return if (systemLang == "ru") SupportedLanguages.RU else SupportedLanguages.EN
    }
}