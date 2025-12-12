package com.nokaori.genshinaibuilder.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}