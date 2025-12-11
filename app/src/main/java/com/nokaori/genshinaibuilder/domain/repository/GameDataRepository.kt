package com.nokaori.genshinaibuilder.domain.repository

interface GameDataRepository {
    // Возвращаем Result, чтобы ViewModel знала, успешно прошло или нет
    suspend fun updateCharacters(): Result<Unit>
}