package com.nokaori.genshinaibuilder.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.nokaori.genshinaibuilder.domain.model.GestureSettings
import com.nokaori.genshinaibuilder.domain.repository.GestureSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class GestureSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GestureSettingsRepository {

    private object PreferencesKeys {
        val SHAKE_ENABLED = booleanPreferencesKey("shake_enabled")
        val SHAKE_SENSITIVITY = floatPreferencesKey("shake_sensitivity")
        
        val DOUBLE_TAP_ENABLED = booleanPreferencesKey("double_tap_enabled")
        val DOUBLE_TAP_SENSITIVITY = floatPreferencesKey("double_tap_sensitivity")
        
        val TILT_ENABLED = booleanPreferencesKey("tilt_enabled")
        val TILT_SENSITIVITY = floatPreferencesKey("tilt_sensitivity")
    }

    override val gestureSettings: Flow<GestureSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            GestureSettings(
                isShakeEnabled = preferences[PreferencesKeys.SHAKE_ENABLED] ?: true,
                shakeSensitivity = preferences[PreferencesKeys.SHAKE_SENSITIVITY] ?: GestureSettings.DEFAULT_SHAKE_SENSITIVITY,
                isDoubleTapEnabled = preferences[PreferencesKeys.DOUBLE_TAP_ENABLED] ?: true,
                doubleTapSensitivity = preferences[PreferencesKeys.DOUBLE_TAP_SENSITIVITY] ?: GestureSettings.DEFAULT_DOUBLE_TAP_SENSITIVITY,
                isTiltEnabled = preferences[PreferencesKeys.TILT_ENABLED] ?: true,
                tiltSensitivity = preferences[PreferencesKeys.TILT_SENSITIVITY] ?: GestureSettings.DEFAULT_TILT_SENSITIVITY
            )
        }

    override suspend fun setShakeEnabled(isEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHAKE_ENABLED] = isEnabled }
    }

    override suspend fun setShakeSensitivity(sensitivity: Float) {
        context.dataStore.edit { it[PreferencesKeys.SHAKE_SENSITIVITY] = sensitivity }
    }

    override suspend fun setDoubleTapEnabled(isEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DOUBLE_TAP_ENABLED] = isEnabled }
    }

    override suspend fun setDoubleTapSensitivity(sensitivity: Float) {
        context.dataStore.edit { it[PreferencesKeys.DOUBLE_TAP_SENSITIVITY] = sensitivity }
    }

    override suspend fun setTiltEnabled(isEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.TILT_ENABLED] = isEnabled }
    }

    override suspend fun setTiltSensitivity(sensitivity: Float) {
        context.dataStore.edit { it[PreferencesKeys.TILT_SENSITIVITY] = sensitivity }
    }
}