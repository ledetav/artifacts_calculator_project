package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateGameDataUseCase @Inject constructor(private val repository: GameDataRepository) {
    operator fun invoke(): Flow<SyncStatus> {
        return repository.updateGameData()
    }
}