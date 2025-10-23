package com.nokaori.genshinaibuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.manager.ThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val themeManager: ThemeManager) : ViewModel() {
    val isDarkTheme: StateFlow<Boolean> = themeManager.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    fun toggleTheme() {
        viewModelScope.launch {
            val newThemeValue = !isDarkTheme.value
            themeManager.setTheme(newThemeValue)
        }
    }
}