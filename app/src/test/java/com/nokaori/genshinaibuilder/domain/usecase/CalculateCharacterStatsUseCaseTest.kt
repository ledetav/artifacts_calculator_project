package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.CharacterPromotion
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateCharacterStatsUseCaseTest {
    private lateinit var useCase: CalculateCharacterStatsUseCase

    @Before
    fun setup() {
        useCase = CalculateCharacterStatsUseCase()
    }

    @Test
    fun invoke_withLevel1NoAscension_returnsBaseStats() {
        val character = Character(
            id = 1,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            iconUrl = "",
            baseHp = 1000f,
            baseAtk = 100f,
            baseDef = 50f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "test"
        )
        val userCharacter = UserCharacter(
            id = 1,
            character = character,
            level = 1,
            ascension = 0,
            constellation = 0,
            talentNormalLevel = 1,
            talentSkillLevel = 1,
            talentBurstLevel = 1
        )
        val curve = StatCurve(id = "test", points = mapOf(1 to 1.0f))
        val promotions = emptyList<CharacterPromotion>()

        val result = useCase(character, userCharacter, curve, promotions)

        assertEquals(1000f, result.maxHp)
        assertEquals(100f, result.atk)
        assertEquals(50f, result.def)
    }

    @Test
    fun invoke_withHigherLevel_appliesCurveMultiplier() {
        val character = Character(
            id = 1,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            iconUrl = "",
            baseHp = 1000f,
            baseAtk = 100f,
            baseDef = 50f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "test"
        )
        val userCharacter = UserCharacter(
            id = 1,
            character = character,
            level = 90,
            ascension = 0,
            constellation = 0,
            talentNormalLevel = 1,
            talentSkillLevel = 1,
            talentBurstLevel = 1
        )
        val curve = StatCurve(id = "test", points = mapOf(90 to 2.0f))
        val promotions = emptyList<CharacterPromotion>()

        val result = useCase(character, userCharacter, curve, promotions)

        assertEquals(2000f, result.maxHp)
        assertEquals(200f, result.atk)
        assertEquals(100f, result.def)
    }

    @Test
    fun invoke_withAscension_addsPromotionBonus() {
        val character = Character(
            id = 1,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            iconUrl = "",
            baseHp = 1000f,
            baseAtk = 100f,
            baseDef = 50f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "test"
        )
        val userCharacter = UserCharacter(
            id = 1,
            character = character,
            level = 20,
            ascension = 1,
            constellation = 0,
            talentNormalLevel = 1,
            talentSkillLevel = 1,
            talentBurstLevel = 1
        )
        val curve = StatCurve(id = "test", points = mapOf(20 to 1.5f))
        val promotions = listOf(
            CharacterPromotion(
                ascensionLevel = 1,
                addHp = 100f,
                addAtk = 20f,
                addDef = 10f,
                ascensionStatValue = 5f
            )
        )

        val result = useCase(character, userCharacter, curve, promotions)

        assertEquals(1600f, result.maxHp)
        assertEquals(170f, result.atk)
        assertEquals(85f, result.def)
        assertEquals(5f, result.ascensionStatValue)
    }

    @Test
    fun invoke_withNullUserCharacter_usesDefaultLevel1() {
        val character = Character(
            id = 1,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            iconUrl = "",
            baseHp = 1000f,
            baseAtk = 100f,
            baseDef = 50f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "test"
        )
        val curve = StatCurve(id = "test", points = mapOf(1 to 1.0f))
        val promotions = emptyList<CharacterPromotion>()

        val result = useCase(character, null, curve, promotions)

        assertEquals(1000f, result.maxHp)
        assertEquals(100f, result.atk)
        assertEquals(50f, result.def)
    }

    @Test
    fun invoke_withNullCurve_usesDefaultMultiplier() {
        val character = Character(
            id = 1,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            iconUrl = "",
            baseHp = 1000f,
            baseAtk = 100f,
            baseDef = 50f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "test"
        )
        val userCharacter = UserCharacter(
            id = 1,
            character = character,
            level = 90,
            ascension = 0,
            constellation = 0,
            talentNormalLevel = 1,
            talentSkillLevel = 1,
            talentBurstLevel = 1
        )
        val promotions = emptyList<CharacterPromotion>()

        val result = useCase(character, userCharacter, null, promotions)

        assertEquals(1000f, result.maxHp)
        assertEquals(100f, result.atk)
        assertEquals(50f, result.def)
    }
}
