package com.nokaori.genshinaibuilder.domain.model

sealed interface SyncStatus {
    data object Idle : SyncStatus
    
    data class InProgress(
        val message: String,
        val progress: Float,
        val logs: List<String>
    ) : SyncStatus
    
    data class Success(
        val summary: String,
        val fullLogs: List<String>
    ) : SyncStatus
    
    data class Error(val message: String) : SyncStatus
}