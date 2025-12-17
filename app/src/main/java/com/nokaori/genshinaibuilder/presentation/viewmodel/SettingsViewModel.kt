package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val gameDataRepository: GameDataRepository
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    fun updateDatabase() {
        viewModelScope.launch {
            gameDataRepository.updateGameData().collect { status ->
                _syncStatus.value = status
            }
        }
    }
}