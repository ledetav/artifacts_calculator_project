package com.nokaori.genshinaibuilder.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val appLanguage: Flow<String>
    suspend fun setAppLanguage(language: String)
}