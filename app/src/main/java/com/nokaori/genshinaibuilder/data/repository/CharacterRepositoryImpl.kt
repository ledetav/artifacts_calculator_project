package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.CharacterConstellation
import com.nokaori.genshinaibuilder.domain.model.CharacterPromotion
import com.nokaori.genshinaibuilder.domain.model.CharacterTalent
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor (
    private val characterDao: CharacterDao,
    private val userDao: UserDao,
    private val statCurveDao: StatCurveDao,
    private val themeRepository: ThemeRepository
) : CharacterRepository {

    override fun getCharacters(): Flow<List<Character>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            characterDao.getCharactersWithOwnership(language).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun getAllCharacterUrls(): List<String> {
        val language = themeRepository.appLanguage.first()
        return characterDao.getAllCharacterUrls(language)
    }

    override suspend fun getCharacterById(id: Int): Character? {
        val language = themeRepository.appLanguage.first()
        val entity = characterDao.getCharacterById(id, language) ?: return null
        val isOwned = userDao.isCharacterOwned(id)
        return entity.toDomain(isOwned = isOwned)
    }

    override suspend fun toggleCharacterOwnership(characterId: Int) {
        val existing = userDao.getUserCharacterByEncyclopediaId(characterId)
        if (existing != null) {
            userDao.deleteUserCharacter(existing)
        } else {
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

    override fun getTalents(characterId: Int): Flow<List<CharacterTalent>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            characterDao.getTalents(characterId, language).map { list ->
                list.map { entity ->
                    CharacterTalent(
                        name = entity.name,
                        description = entity.description,
                        iconUrl = entity.iconUrl,
                        type = entity.type,
                        attributes = entity.scalingAttributes
                    )
                }
            }
        }
    }
    
    override fun getConstellations(characterId: Int): Flow<List<CharacterConstellation>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            characterDao.getConstellations(characterId, language).map { list ->
                list.map { entity ->
                    CharacterConstellation(
                        order = entity.order,
                        name = entity.name,
                        description = entity.description,
                        iconUrl = entity.iconUrl
                    )
                }
            }
        }
    }

    override suspend fun getCharacterPromotions(characterId: Int): List<CharacterPromotion> {
        val language = themeRepository.appLanguage.first()
        val entities = characterDao.getPromotionsForCharacter(characterId, language)
        return entities.map { entity ->
            CharacterPromotion(
                ascensionLevel = entity.ascensionLevel,
                addHp = entity.addHp,
                addAtk = entity.addAtk,
                addDef = entity.addDef,
                ascensionStatValue = entity.ascensionStatValue
            )
        }
    }

    override suspend fun getStatCurve(curveId: String): StatCurve? {
        val entity = statCurveDao.getCurve(curveId) ?: return null
        return StatCurve(
            id = entity.id,
            points = entity.points
        )
    }

    override suspend fun getCharacterCount(language: String): Int {
        return characterDao.getCharacterCountByLanguage(language)
    }
}
