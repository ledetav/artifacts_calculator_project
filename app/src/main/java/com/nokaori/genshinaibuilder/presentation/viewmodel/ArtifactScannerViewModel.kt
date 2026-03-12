package com.nokaori.genshinaibuilder.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.util.ArtifactOcrParser
import com.nokaori.genshinaibuilder.domain.util.ArtifactTextRecognizer
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
import com.nokaori.genshinaibuilder.presentation.util.ScanSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtifactScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val recognizer = ArtifactTextRecognizer(context)

    private val _scannerState = MutableStateFlow(ArtifactScannerState())
    val scannerState: StateFlow<ArtifactScannerState> = _scannerState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ScannerUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun scanImage(uri: Uri) {
        val currentState = _scannerState.value
        if (currentState.isProcessing || currentState.result is ScannerResult.Success) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.update { it.copy(isProcessing = true, result = ScannerResult.Scanning) }
            try {
                delay(800)
                
                val rawText = recognizer.extractTextFromUri(uri)
                if (!rawText.isNullOrBlank()) {
                    val parsedData = ArtifactOcrParser.parse(rawText)
                    _scannerState.update { it.copy(
                        isProcessing = false,
                        result = ScannerResult.Success(parsedData)
                    )}
                } else {
                    _scannerState.update { it.copy(
                        isProcessing = false,
                        result = ScannerResult.Error("Текст не найден. Попробуйте другой скриншот.")
                    )}
                }
            } catch (e: Exception) {
                _scannerState.update { it.copy(
                    isProcessing = false,
                    result = ScannerResult.Error(e.message ?: "Ошибка распознавания")
                )}
            }
        }
    }

    fun scanMultipleImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        if (_scannerState.value.isProcessing) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.update { it.copy(
                isProcessing = true,
                totalToProcess = uris.size,
                currentProcessingIndex = 0,
                result = ScannerResult.Scanning
            )}
            
            val results = mutableListOf<ParsedArtifactData>()
            
            try {
                uris.forEachIndexed { index, uri ->
                    _scannerState.update { it.copy(
                        currentProcessingIndex = index + 1
                    )}
                    
                    try {
                        delay(300)
                        
                        val rawText = recognizer.extractTextFromUri(uri)
                        if (!rawText.isNullOrBlank()) {
                            val parsedData = ArtifactOcrParser.parse(rawText)
                            if (parsedData.slot != null && parsedData.mainStatType != null) {
                                results.add(parsedData)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                if (results.isNotEmpty()) {
                    _scannerState.update { it.copy(
                        isProcessing = false,
                        result = ScannerResult.BatchSuccess(results)
                    )}
                } else {
                    _scannerState.update { it.copy(
                        isProcessing = false,
                        result = ScannerResult.Error("Не удалось распознать ни один артефакт.")
                    )}
                    _uiEvent.emit(ScannerUiEvent.ShowError("Не удалось распознать артефакты"))
                }
            } catch (e: Exception) {
                _scannerState.update { it.copy(
                    isProcessing = false,
                    result = ScannerResult.Error(e.message ?: "Ошибка при обработке пакета")
                )}
                _uiEvent.emit(ScannerUiEvent.ShowError(e.message ?: "Ошибка при обработке пакета"))
            }
        }
    }

    fun onProcessMultipleImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        if (_scannerState.value.isProcessing) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.update { 
                it.copy(
                    isProcessing = true, 
                    totalToProcess = uris.size, 
                    currentProcessingIndex = 0,
                    result = ScannerResult.Scanning
                ) 
            }

            val parsedArtifacts = mutableListOf<ParsedArtifactData>()

            for ((index, uri) in uris.withIndex()) {
                _scannerState.update { it.copy(currentProcessingIndex = index + 1) }
                
                try {
                    delay(300)
                    
                    val rawText = recognizer.extractTextFromUri(uri)
                    if (!rawText.isNullOrBlank()) {
                        val result = ArtifactOcrParser.parse(rawText)
                        
                        if (isValidParsedArtifact(result)) {
                            parsedArtifacts.add(result)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            _scannerState.update { it.copy(isProcessing = false) }

            if (parsedArtifacts.isNotEmpty()) {
                navigateToEditorWithBatch(parsedArtifacts)
            } else {
                _scannerState.update { it.copy(
                    result = ScannerResult.Error("Не удалось распознать ни один артефакт.")
                )}
                _uiEvent.emit(ScannerUiEvent.ShowError("Не удалось распознать артефакты"))
            }
        }
    }

    private fun isValidParsedArtifact(artifact: ParsedArtifactData): Boolean {
        return artifact.slot != null && artifact.mainStatType != null
    }

    private fun navigateToEditorWithBatch(artifacts: List<ParsedArtifactData>) {
        // Сохраняем пакет в ScanSessionManager
        ScanSessionManager.setBatch(artifacts)
        
        viewModelScope.launch {
            // Отправляем событие навигации без аргументов
            _uiEvent.emit(ScannerUiEvent.NavigateToEditor)
        }
    }

    fun resetState() {
        _scannerState.value = ArtifactScannerState()
    }
}

data class ArtifactScannerState(
    val isProcessing: Boolean = false,
    val totalToProcess: Int = 0,
    val currentProcessingIndex: Int = 0,
    val result: ScannerResult = ScannerResult.Idle
) {
    val progress: Float get() = if (totalToProcess > 0) {
        currentProcessingIndex.toFloat() / totalToProcess.toFloat()
    } else {
        0f
    }
    
    val displayProgress: String get() = if (totalToProcess > 0) {
        "$currentProcessingIndex / $totalToProcess"
    } else {
        ""
    }
}

sealed class ScannerResult {
    object Idle : ScannerResult()
    object Scanning : ScannerResult()
    data class Success(val data: ParsedArtifactData) : ScannerResult()
    data class BatchSuccess(val data: List<ParsedArtifactData>) : ScannerResult()
    data class Error(val message: String) : ScannerResult()
}

sealed class ScannerUiEvent {
    data class ShowError(val message: String) : ScannerUiEvent()
    data object NavigateToEditor : ScannerUiEvent()
    data class BatchProcessingComplete(val artifacts: List<ParsedArtifactData>) : ScannerUiEvent()
}

// Для обратной совместимости
sealed class ScannerState {
    object Idle : ScannerState()
    object Scanning : ScannerState()
    data class Success(val data: ParsedArtifactData) : ScannerState()
    data class BatchSuccess(val data: List<ParsedArtifactData>) : ScannerState()
    data class Error(val message: String) : ScannerState()
}

data class BatchProgress(
    val current: Int,
    val total: Int
)
