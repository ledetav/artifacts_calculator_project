package com.nokaori.genshinaibuilder.domain.repository

interface GameDataRepository {
    suspend fun updateGameData(): Result<Unit>
}