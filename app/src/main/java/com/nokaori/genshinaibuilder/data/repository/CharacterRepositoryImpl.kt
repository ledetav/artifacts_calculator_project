package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl(
    private val characterDao: CharacterDao, // Энциклопедия
    private val userDao: UserDao            // Инвентарь
) : CharacterRepository {

    override fun getCharacters(): Flow<List<Character>> {
        return characterDao.getCharactersWithOwnership().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        // Для детального просмотра пока просто берем из энциклопедии
        // ЗАМЕТКА: можно тоже сделать join, чтобы узнать уровень прокачки
        val entity = characterDao.getCharacterById(id) ?: return null

        // Проверяем владение отдельно (ЗАМЕТКА: написать еще один метод в DAO)
        val userChar = userDao.getUserCharacterByEncyclopediaId(id)

        // Создаем временную структуру для маппера
        val withOwnership = com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership(
            character = entity,
            isOwned = userChar != null
        )
        return withOwnership.toDomain()
    }

    override suspend fun toggleCharacterOwnership(characterId: Int) {
        val existingUserChar = userDao.getUserCharacterByEncyclopediaId(characterId)

        if (existingUserChar != null) {
            // Если есть -> Удаляем
            userDao.deleteUserCharacter(existingUserChar)
        } else {
            // Если нет -> Добавляем
            // Создаем с дефолтными статами (1 уровень)
            val newUserChar = UserCharacterEntity(
                id = 0,
                characterId = characterId,
                level = 1,
                ascension = 0,
                constellation = 0,
                talentNormalLevel = 1,
                talentSkillLevel = 1,
                talentBurstLevel = 1
            )
            userDao.insertUserCharacter(newUserChar)
        }
    }
}