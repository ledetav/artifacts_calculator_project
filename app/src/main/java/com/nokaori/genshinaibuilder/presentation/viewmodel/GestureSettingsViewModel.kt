package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.GestureSettings
import com.nokaori.genshinaibuilder.domain.repository.GestureSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestureSettingsViewModel @Inject constructor(
    private val repository: GestureSettingsRepository
) : ViewModel() {

    // Преобразуем Flow из репозитория в StateFlow для удобного использования в Compose
    val settings: StateFlow<GestureSettings> = repository.gestureSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GestureSettings() // Дефолтные значения до первой загрузки
        )

    fun setShakeEnabled(isEnabled: Boolean) {
        viewModelScope.launch { repository.setShakeEnabled(isEnabled) }
    }

    fun setShakeSensitivity(sensitivity: Float) {
        viewModelScope.launch { repository.setShakeSensitivity(sensitivity) }
    }

    fun setDoubleTapEnabled(isEnabled: Boolean) {
        viewModelScope.launch { repository.setDoubleTapEnabled(isEnabled) }
    }

    fun setDoubleTapSensitivity(sensitivity: Float) {
        viewModelScope.launch { repository.setDoubleTapSensitivity(sensitivity) }
    }

    fun setTiltEnabled(isEnabled: Boolean) {
        viewModelScope.launch { repository.setTiltEnabled(isEnabled) }
    }

    fun setTiltSensitivity(sensitivity: Float) {
        viewModelScope.launch { repository.setTiltSensitivity(sensitivity) }
    }
}