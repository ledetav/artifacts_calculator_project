package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.GestureSettings
import kotlinx.coroutines.flow.Flow

interface GestureSettingsRepository {
    val gestureSettings: Flow<GestureSettings>

    suspend fun setShakeEnabled(isEnabled: Boolean)
    suspend fun setShakeSensitivity(sensitivity: Float)

    suspend fun setDoubleTapEnabled(isEnabled: Boolean)
    suspend fun setDoubleTapSensitivity(sensitivity: Float)

    suspend fun setTiltEnabled(isEnabled: Boolean)
    suspend fun setTiltSensitivity(sensitivity: Float)
}