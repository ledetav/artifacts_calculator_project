package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val gameDataRepository: GameDataRepository,
    private val languageRepository: SettingsRepository,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _shouldShowLanguageSyncDialog = MutableStateFlow(false)
    val shouldShowLanguageSyncDialog: StateFlow<Boolean> = _shouldShowLanguageSyncDialog.asStateFlow()

    val appLanguage: StateFlow<String> = languageRepository.appLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    val lastSyncTime: StateFlow<Long> = languageRepository.lastSyncTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    init {
        // Мониторим смену языка и проверяем наличие данных
        viewModelScope.launch {
            languageRepository.appLanguage.collectLatest { newLanguage ->
                try {
                    checkIfDataExistsForLanguage(newLanguage)
                } catch (_: Exception) {
                    // БД может быть ещё не инициализирована при первом запуске — пропускаем
                }
            }
        }
    }

    private suspend fun checkIfDataExistsForLanguage(language: String) {
        val characterCount = characterRepository.getCharacterCount(language)
        if (characterCount == 0) {
            _shouldShowLanguageSyncDialog.value = true
        }
    }

    fun setAppLanguage(languageCode: String) {
        viewModelScope.launch {
            languageRepository.setAppLanguage(languageCode)
        }
    }

    fun dismissLanguageSyncDialog() {
        _shouldShowLanguageSyncDialog.value = false
    }

    fun syncDataForCurrentLanguage() {
        _shouldShowLanguageSyncDialog.value = false
        updateDatabase()
    }

    fun updateDatabase() {
        viewModelScope.launch {
            gameDataRepository.updateGameData().collect { status ->
                _syncStatus.value = status
                if (status is SyncStatus.Success) {
                    languageRepository.setLastSyncTime(System.currentTimeMillis())
                }
            }
        }
    }
}