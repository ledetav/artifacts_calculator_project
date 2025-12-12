package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.usecase.UpdateGameDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateGameDataUseCase: UpdateGameDataUseCase
) : ViewModel() {

    // Состояния UI
    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun updateDatabase() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading

            val result = updateGameDataUseCase()

            result.onSuccess {
                _updateState.value = UpdateState.Success
            }.onFailure { error ->
                _updateState.value = UpdateState.Error(error.localizedMessage ?: "Unknown error")
            }
        }
    }
}