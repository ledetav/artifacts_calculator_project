package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl(
    private val characterDao: CharacterDao,
    private val userDao: UserDao
) : CharacterRepository {

    override fun getCharacters(): Flow<List<Character>> {
        return characterDao.getCharactersWithOwnership().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        val entity = characterDao.getCharacterById(id) ?: return null
        val isOwned = userDao.isCharacterOwned(id)
        return entity.toDomain(isOwned = isOwned)
    }

    override suspend fun toggleCharacterOwnership(characterId: Int) {
        val existing = userDao.getUserCharacterByEncyclopediaId(characterId)
        if (existing != null) {
            userDao.deleteUserCharacter(existing)
        } else {
            // Добавляем с дефолтными статами (1 уровень)
            val newChar = UserCharacterEntity(
                id = 0,
                characterId = characterId,
                level = 1,
                ascension = 0,
                constellation = 0,
                talentNormalLevel = 1,
                talentSkillLevel = 1,
                talentBurstLevel = 1
            )
            userDao.insertUserCharacter(newChar)
        }
    }

    override fun getUserCharacter(encyclopediaId: Int): Flow<UserCharacter?> {
        return userDao.getUserCharacterCompleteByEncyclopediaId(encyclopediaId).map {
            it?.toDomain()
        }
    }
}