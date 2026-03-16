package com.nokaori.genshinaibuilder.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.util.ArtifactOcrParser
import com.nokaori.genshinaibuilder.domain.util.ArtifactTextRecognizer
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
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
    @ApplicationContext private val context: Context,
    private val artifactRepository: ArtifactRepository
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

                val availablePieces = artifactRepository.getAllPiecesForMatching()
                
                val rawText = recognizer.extractTextFromUri(uri)
                if (!rawText.isNullOrBlank()) {
                    val parsedData = ArtifactOcrParser.parse(rawText, availablePieces)
                    if (isValidParsedArtifact(parsedData)) {
                        _scannerState.update { it.copy(
                            isProcessing = false,
                            result = ScannerResult.Success(parsedData)
                        )}
                    } else {
                        _scannerState.update { it.copy(
                            isProcessing = false,
                            result = ScannerResult.Error("Характеристики не распознаны. Убедитесь, что фото четкое.")
                        )}
                    }
                } else {
                    _scannerState.update { it.copy(
                        isProcessing = false,
                        result = ScannerResult.Error("Текст не найден. Попробуйте другой снимок.")
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
        if (uris.isEmpty() || _scannerState.value.isProcessing) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.update { it.copy(
                isProcessing = true,
                totalToProcess = uris.size,
                currentProcessingIndex = 0,
                successfulScans = 0,
                failedScans = 0,
                result = ScannerResult.Scanning
            )}
            
            val results = mutableListOf<ParsedArtifactData>()
            val availablePieces = artifactRepository.getAllPiecesForMatching()
            
            uris.forEachIndexed { index, uri ->
                _scannerState.update { it.copy(
                    currentProcessingIndex = index + 1,
                    currentImageUri = uri
                )}
                
                try {
                    delay(300)
                    val rawText = recognizer.extractTextFromUri(uri)
                    if (!rawText.isNullOrBlank()) {
                        // Передаем куски в парсер
                        val parsedData = ArtifactOcrParser.parse(rawText, availablePieces)
                        if (isValidParsedArtifact(parsedData)) {
                            results.add(parsedData)
                            _scannerState.update { it.copy(successfulScans = it.successfulScans + 1) }
                        } else {
                            _scannerState.update { it.copy(failedScans = it.failedScans + 1) }
                        }
                    } else {
                        _scannerState.update { it.copy(failedScans = it.failedScans + 1) }
                    }
                } catch (e: Exception) {
                    _scannerState.update { it.copy(failedScans = it.failedScans + 1) }
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
                    result = ScannerResult.Error("Не удалось распознать ни один артефакт из пакета.")
                )}
            }
        }
    }

    private fun isValidParsedArtifact(artifact: ParsedArtifactData): Boolean {
        return artifact.slot != null && artifact.mainStatType != null
    }

    fun resetState() {
        _scannerState.value = ArtifactScannerState()
    }
}

data class ArtifactScannerState(
    val isProcessing: Boolean = false,
    val totalToProcess: Int = 0,
    val currentProcessingIndex: Int = 0,
    val successfulScans: Int = 0,
    val failedScans: Int = 0,
    val currentImageUri: Uri? = null,
    val result: ScannerResult = ScannerResult.Idle
) {
    val progress: Float get() = if (totalToProcess > 0) {
        currentProcessingIndex.toFloat() / totalToProcess.toFloat()
    } else 0f
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