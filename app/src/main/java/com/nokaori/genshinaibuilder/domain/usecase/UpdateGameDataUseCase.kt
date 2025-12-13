package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository

class UpdateGameDataUseCase(
    private val repository: GameDataRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.updateCharacters()
    }
}