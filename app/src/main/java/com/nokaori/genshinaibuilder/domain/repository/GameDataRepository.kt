package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface GameDataRepository {
    fun updateGameData(): Flow<SyncStatus>
}