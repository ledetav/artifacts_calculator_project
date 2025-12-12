package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharacters(): Flow<List<Character>>

    suspend fun getCharacterById(id: Int): Character?

    suspend fun toggleCharacterOwnership(characterId: Int)
    fun getUserCharacter(encyclopediaId: Int): Flow<UserCharacter?>
}