package com.nokaori.genshinaibuilder.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
    
    val appLanguage: Flow<String>
    suspend fun setAppLanguage(language: String)
}