package com.nokaori.genshinaibuilder.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.util.ArtifactOcrParser
import com.nokaori.genshinaibuilder.domain.util.ArtifactTextRecognizer
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtifactScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val recognizer = ArtifactTextRecognizer(context)

    private val _scannerState = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val scannerState: StateFlow<ScannerState> = _scannerState.asStateFlow()

    private val _batchProgress = MutableStateFlow<BatchProgress?>(null)
    val batchProgress: StateFlow<BatchProgress?> = _batchProgress.asStateFlow()

    fun scanImage(uri: Uri) {
        // Защита от повторных запусков
        if (_scannerState.value is ScannerState.Scanning || _scannerState.value is ScannerState.Success) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.value = ScannerState.Scanning
            try {
                // Имитируем небольшую задержку для плавности UI лоадера
                delay(800)
                
                val rawText = recognizer.extractTextFromUri(uri)
                if (!rawText.isNullOrBlank()) {
                    val parsedData = ArtifactOcrParser.parse(rawText)
                    _scannerState.value = ScannerState.Success(parsedData)
                } else {
                    _scannerState.value = ScannerState.Error("Текст не найден. Попробуйте другой скриншот.")
                }
            } catch (e: Exception) {
                _scannerState.value = ScannerState.Error(e.message ?: "Ошибка распознавания")
            }
        }
    }

    fun scanMultipleImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        if (_scannerState.value is ScannerState.Scanning) return

        viewModelScope.launch(Dispatchers.IO) {
            _scannerState.value = ScannerState.Scanning
            val results = mutableListOf<ParsedArtifactData>()
            
            try {
                uris.forEachIndexed { index, uri ->
                    // Обновляем прогресс
                    _batchProgress.value = BatchProgress(
                        current = index + 1,
                        total = uris.size
                    )
                    
                    try {
                        // Небольшая задержка между обработками для избежания OutOfMemory
                        delay(300)
                        
                        val rawText = recognizer.extractTextFromUri(uri)
                        if (!rawText.isNullOrBlank()) {
                            val parsedData = ArtifactOcrParser.parse(rawText)
                            results.add(parsedData)
                        }
                    } catch (e: Exception) {
                        // Логируем ошибку, но продолжаем обработку остальных
                        e.printStackTrace()
                    }
                }
                
                if (results.isNotEmpty()) {
                    _scannerState.value = ScannerState.BatchSuccess(results)
                    _batchProgress.value = null
                } else {
                    _scannerState.value = ScannerState.Error("Не удалось распознать ни один артефакт.")
                }
            } catch (e: Exception) {
                _scannerState.value = ScannerState.Error(e.message ?: "Ошибка при обработке пакета")
                _batchProgress.value = null
            }
        }
    }

    fun resetState() {
        _scannerState.value = ScannerState.Idle
        _batchProgress.value = null
    }
}

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
