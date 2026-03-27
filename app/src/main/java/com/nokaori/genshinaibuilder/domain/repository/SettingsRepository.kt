package com.nokaori.genshinaibuilder.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val appLanguage: Flow<String>
    suspend fun setAppLanguage(language: String)

    val lastSyncTime: Flow<Long>
    suspend fun setLastSyncTime(time: Long)
}