package com.nokaori.genshinaibuilder.presentation.util

import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData

/**
 * Singleton для передачи пакета артефактов между экранами.
 * 
 * Используется для обхода ограничений Jetpack Navigation на размер аргументов.
 * Список ParsedArtifactData может быть слишком большим для передачи через Bundle.
 * 
 * Использование:
 * 1. В ArtifactScannerViewModel: ScanSessionManager.setBatch(artifacts)
 * 2. В EditorArtifactViewModel init: val batch = ScanSessionManager.getBatchAndClear()
 */
object ScanSessionManager {
    private var currentBatch: List<ParsedArtifactData>? = null

    /**
     * Сохраняет пакет артефактов для передачи на следующий экран.
     * 
     * @param batch Список распарсенных артефактов
     */
    fun setBatch(batch: List<ParsedArtifactData>) {
        currentBatch = batch
    }

    /**
     * Получает пакет артефактов и очищает память.
     * 
     * @return Сохраненный пакет или пустой список, если ничего не было сохранено
     */
    fun getBatchAndClear(): List<ParsedArtifactData> {
        val batch = currentBatch ?: emptyList()
        currentBatch = null // Очищаем память после получения
        return batch
    }

    /**
     * Проверяет, есть ли сохраненный пакет.
     * 
     * @return true если пакет существует
     */
    fun hasBatch(): Boolean = currentBatch != null

    /**
     * Очищает сохраненный пакет без возврата.
     */
    fun clearBatch() {
        currentBatch = null
    }
}
