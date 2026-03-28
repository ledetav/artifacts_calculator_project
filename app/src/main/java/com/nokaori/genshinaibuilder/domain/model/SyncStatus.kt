package com.nokaori.genshinaibuilder.domain.model

sealed class SyncStatus {
    object Idle : SyncStatus()
    data class InProgress(
        val message: UiText, 
        val progress: Float, 
        val logs: List<UiText>
    ) : SyncStatus()
    
    data class Success(
        val summary: UiText,
        val fullLogs: List<UiText>,
        val newChars: Int = 0,
        val newWeapons: Int = 0,
        val newArtifacts: Int = 0,
        val sampleCharNames: List<String> = emptyList(),
        val sampleWeaponNames: List<String> = emptyList(),
        val sampleArtifactNames: List<String> = emptyList()
    ) : SyncStatus()
    
    data class Error(
        val message: UiText
    ) : SyncStatus()
}