package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.CharacterConstellation
import com.nokaori.genshinaibuilder.domain.model.CharacterPromotion
import com.nokaori.genshinaibuilder.domain.model.CharacterTalent
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(): Flow<List<Character>>
    suspend fun getCharacterById(id: Int): Character?
    suspend fun toggleCharacterOwnership(characterId: Int)
    fun getUserCharacter(encyclopediaId: Int): Flow<UserCharacter?>
    suspend fun getAllCharacterUrls(): List<String>
    fun getTalents(characterId: Int): Flow<List<CharacterTalent>>
    fun getConstellations(characterId: Int): Flow<List<CharacterConstellation>>
    suspend fun getCharacterPromotions(characterId: Int): List<CharacterPromotion>
    suspend fun getStatCurve(curveId: String): StatCurve?
}