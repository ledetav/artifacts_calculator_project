package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.CharacterConstellationEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterTalentEntity
import com.nokaori.genshinaibuilder.data.local.entity.StatCurveEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership
import com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.CharacterConstellation
import com.nokaori.genshinaibuilder.domain.model.CharacterPromotion
import com.nokaori.genshinaibuilder.domain.model.CharacterTalent
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.TalentAttribute
import com.nokaori.genshinaibuilder.domain.model.TalentType
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CharacterRepositoryImplTest {
    @Mock
    private lateinit var characterDao: CharacterDao
    @Mock
    private lateinit var userDao: UserDao
    @Mock
    private lateinit var statCurveDao: StatCurveDao

    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = CharacterRepositoryImpl(characterDao, userDao, statCurveDao)
    }

    @Test
    fun getCharacters_returnsFlowOfCharacters() = runTest {
        val mockCharacter = CharacterWithOwnership(
            character = CharacterEntity(
                id = 1,
                language = "en",
                name = "Nahida",
                element = Element.DENDRO,
                rarity = 5,
                weaponType = WeaponType.CATALYST,
                baseHpLvl1 = 1000f,
                baseAtkLvl1 = 100f,
                baseDefLvl1 = 50f,
                ascensionStatType = StatType.ELEMENTAL_MASTERY,
                curveId = "AVATAR_CURVE_EXP_FAST",
                iconUrl = "url",
                splashUrl = "url"
            ),
            isOwned = true
        )

        whenever(characterDao.getCharactersWithOwnership()).thenReturn(flowOf(listOf(mockCharacter)))

        val result = mutableListOf<Character>()
        repository.getCharacters().collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getCharacterById_withValidId_returnsCharacter() = runTest {
        val mockEntity = CharacterEntity(
            id = 1,
            language = "en",
            name = "Nahida",
            element = Element.DENDRO,
            rarity = 5,
            weaponType = WeaponType.CATALYST,
            baseHpLvl1 = 1000f,
            baseAtkLvl1 = 100f,
            baseDefLvl1 = 50f,
            ascensionStatType = StatType.ELEMENTAL_MASTERY,
            curveId = "AVATAR_CURVE_EXP_FAST",
            iconUrl = "url",
            splashUrl = "url"
        )

        whenever(characterDao.getCharacterById(1)).thenReturn(mockEntity)
        whenever(userDao.isCharacterOwned(1)).thenReturn(true)

        val result = repository.getCharacterById(1)

        assertEquals("Nahida", result?.name)
    }

    @Test
    fun getCharacterById_withInvalidId_returnsNull() = runTest {
        whenever(characterDao.getCharacterById(999)).thenReturn(null)

        val result = repository.getCharacterById(999)

        assertNull(result)
    }

    @Test
    fun toggleCharacterOwnership_withNewCharacter_insertsUserCharacter() = runTest {
        whenever(userDao.getUserCharacterByEncyclopediaId(1)).thenReturn(null)

        repository.toggleCharacterOwnership(1)

        verify(userDao).insertUserCharacter(any())
    }

    @Test
    fun toggleCharacterOwnership_withExistingCharacter_deletesUserCharacter() = runTest {
        val existing = UserCharacterEntity(
            id = 1,
            characterId = 1,
            level = 90,
            ascension = 6,
            constellation = 6,
            talentNormalLevel = 10,
            talentSkillLevel = 10,
            talentBurstLevel = 10
        )

        whenever(userDao.getUserCharacterByEncyclopediaId(1)).thenReturn(existing)

        repository.toggleCharacterOwnership(1)

        verify(userDao).deleteUserCharacter(existing)
    }

    @Test
    fun getAllCharacterUrls_returnsListOfUrls() = runTest {
        val urls = listOf("url1", "url2", "url3")
        whenever(characterDao.getAllCharacterUrls()).thenReturn(urls)

        val result = repository.getAllCharacterUrls()

        assertEquals(urls, result)
    }

    @Test
    fun getUserCharacter_returnsFlowOfUserCharacter() = runTest {
        val mockComplete = UserCharacterComplete(
            userCharacter = UserCharacterEntity(
                id = 1,
                characterId = 1,
                level = 90,
                ascension = 6,
                constellation = 6,
                talentNormalLevel = 10,
                talentSkillLevel = 10,
                talentBurstLevel = 10
            ),
            characterEntity = CharacterEntity(
                id = 1,
                language = "en",
                name = "Nahida",
                element = Element.DENDRO,
                rarity = 5,
                weaponType = WeaponType.CATALYST,
                baseHpLvl1 = 1000f,
                baseAtkLvl1 = 100f,
                baseDefLvl1 = 50f,
                ascensionStatType = StatType.ELEMENTAL_MASTERY,
                curveId = "AVATAR_CURVE_EXP_FAST",
                iconUrl = "url",
                splashUrl = "url"
            )
        )

        whenever(userDao.getUserCharacterCompleteByEncyclopediaId(1)).thenReturn(flowOf(mockComplete))

        val result = mutableListOf<UserCharacter?>()
        repository.getUserCharacter(1).collect { result.add(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getTalents_returnsFlowOfTalents() = runTest {
        val mockTalent = CharacterTalentEntity(
            id = 1,
            characterId = 1,
            language = "en",
            orderIndex = 0,
            type = TalentType.NORMAL_ATTACK,
            name = "Normal Attack",
            description = "Test",
            iconUrl = "url",
            scalingAttributes = emptyList()
        )

        whenever(characterDao.getTalents(1)).thenReturn(flowOf(listOf(mockTalent)))

        val result = mutableListOf<CharacterTalent>()
        repository.getTalents(1).collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getConstellations_returnsFlowOfConstellations() = runTest {
        val mockConstellation = CharacterConstellationEntity(
            characterId = 1,
            language = "en",
            order = 1,
            name = "Constellation 1",
            description = "Test",
            iconUrl = "url",
            talentLevelUpTarget = TalentType.ELEMENTAL_SKILL
        )

        whenever(characterDao.getConstellations(1)).thenReturn(flowOf(listOf(mockConstellation)))

        val result = mutableListOf<CharacterConstellation>()
        repository.getConstellations(1).collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getCharacterPromotions_returnsListOfPromotions() = runTest {
        val mockPromotion = CharacterPromotionEntity(
            characterId = 1,
            language = "en",
            ascensionLevel = 1,
            addHp = 100f,
            addAtk = 10f,
            addDef = 20f,
            ascensionStatValue = 5f
        )

        whenever(characterDao.getPromotionsForCharacter(1)).thenReturn(listOf(mockPromotion))

        val result = repository.getCharacterPromotions(1)

        assertEquals(1, result.size)
    }

    @Test
    fun getStatCurve_withValidCurveId_returnsCurve() = runTest {
        val curve = StatCurveEntity(
            id = "AVATAR_CURVE_EXP_FAST",
            points = mapOf(0 to 0f, 1 to 1000f)
        )

        whenever(statCurveDao.getCurve("AVATAR_CURVE_EXP_FAST")).thenReturn(curve)

        val result = repository.getStatCurve("AVATAR_CURVE_EXP_FAST")

        assertEquals(curve.id, result?.id)
    }

    @Test
    fun getStatCurve_withNonExistentCurveId_returnsNull() = runTest {
        whenever(statCurveDao.getCurve("NONEXISTENT")).thenReturn(null)

        val result = repository.getStatCurve("NONEXISTENT")

        assertNull(result)
    }
}
