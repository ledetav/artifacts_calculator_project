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
        val fullLogs: List<UiText>
    ) : SyncStatus()
    
    data class Error(
        val message: UiText
    ) : SyncStatus()
}