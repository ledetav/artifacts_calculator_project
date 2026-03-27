package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership
import com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class CharacterMapperTest {

    @Test
    fun testCharacterWithOwnershipToDomain() {
        val characterEntity = CharacterEntity(
            id = 1,
            language = "en",
            name = "Ayaka",
            element = Element.CRYO,
            weaponType = WeaponType.SWORD,
            rarity = 5,
            baseHpLvl1 = 342f,
            baseAtkLvl1 = 23f,
            baseDefLvl1 = 20f,
            ascensionStatType = StatType.CRIT_DMG,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/ayaka.png",
            splashUrl = "https://example.com/ayaka_splash.png"
        )

        val characterWithOwnership = CharacterWithOwnership(
            character = characterEntity,
            isOwned = true
        )

        val result = characterWithOwnership.toDomain()

        assertEquals(1, result.id)
        assertEquals("Ayaka", result.name)
        assertEquals(Element.CRYO, result.element)
        assertEquals(WeaponType.SWORD, result.weaponType)
        assertEquals(Rarity.FIVE_STARS, result.rarity)
        assertTrue(result.isOwned)
    }

    @Test
    fun testCharacterWithOwnershipToDomainNotOwned() {
        val characterEntity = CharacterEntity(
            id = 2,
            language = "en",
            name = "Ganyu",
            element = Element.CRYO,
            weaponType = WeaponType.BOW,
            rarity = 5,
            baseHpLvl1 = 335f,
            baseAtkLvl1 = 48f,
            baseDefLvl1 = 20f,
            ascensionStatType = StatType.CRIT_DMG,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/ganyu.png",
            splashUrl = "https://example.com/ganyu_splash.png"
        )

        val characterWithOwnership = CharacterWithOwnership(
            character = characterEntity,
            isOwned = false
        )

        val result = characterWithOwnership.toDomain()

        assertEquals(2, result.id)
        assertEquals("Ganyu", result.name)
        assertFalse(result.isOwned)
    }

    @Test
    fun testCharacterEntityToDomainWithOwnedFlag() {
        val characterEntity = CharacterEntity(
            id = 3,
            language = "en",
            name = "Fischl",
            element = Element.ELECTRO,
            weaponType = WeaponType.BOW,
            rarity = 4,
            baseHpLvl1 = 287f,
            baseAtkLvl1 = 24f,
            baseDefLvl1 = 18f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/fischl.png",
            splashUrl = "https://example.com/fischl_splash.png"
        )

        val result = characterEntity.toDomain(isOwned = true)

        assertEquals(3, result.id)
        assertEquals("Fischl", result.name)
        assertEquals(Rarity.FOUR_STARS, result.rarity)
        assertTrue(result.isOwned)
    }

    @Test
    fun testUserCharacterCompleteToDomain() {
        val characterEntity = CharacterEntity(
            id = 4,
            language = "en",
            name = "Zhongli",
            element = Element.GEO,
            weaponType = WeaponType.POLEARM,
            rarity = 5,
            baseHpLvl1 = 251f,
            baseAtkLvl1 = 19f,
            baseDefLvl1 = 30f,
            ascensionStatType = StatType.GEO_DAMAGE_BONUS,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/zhongli.png",
            splashUrl = "https://example.com/zhongli_splash.png"
        )

        val userCharacterEntity = UserCharacterEntity(
            id = 1,
            characterId = 4,
            level = 90,
            ascension = 6,
            constellation = 2,
            talentNormalLevel = 9,
            talentSkillLevel = 9,
            talentBurstLevel = 10
        )

        val userCharacterComplete = UserCharacterComplete(
            userCharacter = userCharacterEntity,
            characterEntity = characterEntity
        )

        val result = userCharacterComplete.toDomain()

        assertEquals(1, result.id)
        assertEquals("Zhongli", result.character.name)
        assertEquals(90, result.level)
        assertEquals(6, result.ascension)
        assertEquals(2, result.constellation)
        assertEquals(9, result.talentNormalLevel)
        assertEquals(9, result.talentSkillLevel)
        assertEquals(10, result.talentBurstLevel)
        assertTrue(result.character.isOwned)
    }

    @Test
    fun testUserCharacterCompleteToDomainWithLowLevelCharacter() {
        val characterEntity = CharacterEntity(
            id = 5,
            language = "en",
            name = "Barbara",
            element = Element.HYDRO,
            weaponType = WeaponType.CATALYST,
            rarity = 4,
            baseHpLvl1 = 287f,
            baseAtkLvl1 = 17f,
            baseDefLvl1 = 19f,
            ascensionStatType = StatType.HP_PERCENT,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/barbara.png",
            splashUrl = "https://example.com/barbara_splash.png"
        )

        val userCharacterEntity = UserCharacterEntity(
            id = 2,
            characterId = 5,
            level = 20,
            ascension = 1,
            constellation = 0,
            talentNormalLevel = 1,
            talentSkillLevel = 1,
            talentBurstLevel = 1
        )

        val userCharacterComplete = UserCharacterComplete(
            userCharacter = userCharacterEntity,
            characterEntity = characterEntity
        )

        val result = userCharacterComplete.toDomain()

        assertEquals(2, result.id)
        assertEquals(20, result.level)
        assertEquals(1, result.ascension)
        assertEquals(0, result.constellation)
        assertEquals(1, result.talentNormalLevel)
    }

    @Test
    fun testCharacterEntityToDomainPreservesAllStats() {
        val characterEntity = CharacterEntity(
            id = 6,
            language = "en",
            name = "Hu Tao",
            element = Element.PYRO,
            weaponType = WeaponType.POLEARM,
            rarity = 5,
            baseHpLvl1 = 251f,
            baseAtkLvl1 = 38f,
            baseDefLvl1 = 20f,
            ascensionStatType = StatType.CRIT_DMG,
            curveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/hutao.png",
            splashUrl = "https://example.com/hutao_splash.png"
        )

        val result = characterEntity.toDomain(isOwned = false)

        assertEquals(251f, result.baseHp, 0.01f)
        assertEquals(38f, result.baseAtk, 0.01f)
        assertEquals(20f, result.baseDef, 0.01f)
        assertEquals(StatType.CRIT_DMG, result.ascensionStatType)
        assertEquals("GROW_CURVE_ATTACK_S4", result.curveId)
    }
}
