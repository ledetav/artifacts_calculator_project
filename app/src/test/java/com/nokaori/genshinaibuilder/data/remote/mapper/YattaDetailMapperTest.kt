package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaUpgradeDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaPropDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaPromoteDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaTalentDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaPromoteLevelDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaConstellationDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.TalentType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class YattaDetailMapperTest {

    @Test
    fun testCharacterEntityUpdateWithDetails() {
        val characterEntity = CharacterEntity(
            id = 1,
            name = "Ayaka",
            element = Element.CRYO,
            weaponType = WeaponType.SWORD,
            rarity = 5,
            baseHpLvl1 = 0f,
            baseAtkLvl1 = 0f,
            baseDefLvl1 = 0f,
            ascensionStatType = StatType.UNKNOWN,
            curveId = "",
            iconUrl = "https://example.com/ayaka.png",
            splashUrl = "https://example.com/ayaka_splash.png"
        )

        val detailDto = YattaAvatarDetailDto(
            id = "10000002",
            specialProp = "FIGHT_PROP_CRITICAL_HURT",
            upgrade = YattaUpgradeDto(
                props = listOf(
                    YattaPropDto(
                        propType = "FIGHT_PROP_BASE_HP",
                        initValue = 342.0,
                        curveId = "GROW_CURVE_HP_S4"
                    ),
                    YattaPropDto(
                        propType = "FIGHT_PROP_BASE_ATTACK",
                        initValue = 23.0,
                        curveId = "GROW_CURVE_ATTACK_S4"
                    ),
                    YattaPropDto(
                        propType = "FIGHT_PROP_BASE_DEFENSE",
                        initValue = 20.0,
                        curveId = "GROW_CURVE_DEF_S4"
                    )
                ),
                promote = null
            ),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = characterEntity.updateWithDetails(detailDto)

        assertEquals(342f, result.baseHpLvl1, 0.01f)
        assertEquals(23f, result.baseAtkLvl1, 0.01f)
        assertEquals(20f, result.baseDefLvl1, 0.01f)
        assertEquals(StatType.CRIT_DMG, result.ascensionStatType)
        assertEquals("GROW_CURVE_ATTACK_S4", result.curveId)
    }

    @Test
    fun testCharacterEntityUpdateWithDetailsWithoutProps() {
        val characterEntity = CharacterEntity(
            id = 2,
            name = "Test",
            element = Element.PYRO,
            weaponType = WeaponType.CLAYMORE,
            rarity = 5,
            baseHpLvl1 = 0f,
            baseAtkLvl1 = 0f,
            baseDefLvl1 = 0f,
            ascensionStatType = StatType.UNKNOWN,
            curveId = "",
            iconUrl = "https://example.com/test.png",
            splashUrl = "https://example.com/test_splash.png"
        )

        val detailDto = YattaAvatarDetailDto(
            id = "10000001",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = characterEntity.updateWithDetails(detailDto)

        assertEquals(0f, result.baseHpLvl1, 0.01f)
        assertEquals(0f, result.baseAtkLvl1, 0.01f)
        assertEquals(0f, result.baseDefLvl1, 0.01f)
    }

    @Test
    fun testMapPromotionsWithValidData() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000003",
            specialProp = "FIGHT_PROP_CRITICAL_HURT",
            upgrade = YattaUpgradeDto(
                props = emptyList(),
                promote = listOf(
                    YattaPromoteDto(
                        level = 1,
                        addProps = mapOf(
                            "FIGHT_PROP_BASE_HP" to 50.0,
                            "FIGHT_PROP_BASE_ATTACK" to 5.0,
                            "FIGHT_PROP_BASE_DEFENSE" to 3.0,
                            "FIGHT_PROP_CRITICAL_HURT" to 4.8
                        )
                    ),
                    YattaPromoteDto(
                        level = 2,
                        addProps = mapOf(
                            "FIGHT_PROP_BASE_HP" to 100.0,
                            "FIGHT_PROP_BASE_ATTACK" to 10.0,
                            "FIGHT_PROP_BASE_DEFENSE" to 6.0,
                            "FIGHT_PROP_CRITICAL_HURT" to 9.6
                        )
                    )
                )
            ),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = mapPromotions(1, "en", detailDto)

        assertEquals(2, result.size)
        assertEquals(1, result[0].ascensionLevel)
        assertEquals(50f, result[0].addHp, 0.01f)
        assertEquals(5f, result[0].addAtk, 0.01f)
        assertEquals(3f, result[0].addDef, 0.01f)
        assertEquals(4.8f, result[0].ascensionStatValue, 0.01f)
    }

    @Test
    fun testMapPromotionsWithNullPromote() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000004",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = mapPromotions(2, "en", detailDto)

        assertEquals(0, result.size)
    }

    @Test
    fun testMapPromotionsWithMissingProps() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000005",
            specialProp = "FIGHT_PROP_CRITICAL_HURT",
            upgrade = YattaUpgradeDto(
                props = emptyList(),
                promote = listOf(
                    YattaPromoteDto(
                        level = 1,
                        addProps = null
                    )
                )
            ),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = mapPromotions(3, "en", detailDto)

        assertEquals(1, result.size)
        assertEquals(0f, result[0].addHp, 0.01f)
        assertEquals(0f, result[0].addAtk, 0.01f)
    }

    @Test
    fun testMapTalentsAndConstellationsWithValidData() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000006",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = mapOf(
                "1" to YattaTalentDto(
                    name = "Normal Attack",
                    description = "Test description",
                    icon = "Skill_A_01",
                    type = 0,
                    promote = mapOf(
                        "1" to YattaPromoteLevelDto(
                            description = listOf("Damage: {param1:F1P}%"),
                            params = listOf(50.0, 55.0, 60.0)
                        )
                    ),
                    linkedConstellations = null
                ),
                "2" to YattaTalentDto(
                    name = "Elemental Skill",
                    description = "Test skill",
                    icon = "Skill_S_01",
                    type = 1,
                    promote = null,
                    linkedConstellations = null
                )
            ),
            constellations = mapOf(
                "1" to YattaConstellationDto(
                    name = "Const 1",
                    description = "Test const",
                    icon = "UI_Talent_Const_01",
                    extraData = null
                )
            ),
            dictionary = null
        )

        val (talents, constellations) = mapTalentsAndConstellations(1, "en", detailDto)

        assertEquals(2, talents.size)
        assertEquals(1, constellations.size)
        assertEquals(TalentType.NORMAL_ATTACK, talents[0].type)
        assertEquals(TalentType.ELEMENTAL_SKILL, talents[1].type)
    }

    @Test
    fun testMapTalentsAndConstellationsWithoutConstellations() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000007",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = mapOf(
                "1" to YattaTalentDto(
                    name = "Normal Attack",
                    description = "Test",
                    icon = "Skill_A_01",
                    type = 0,
                    promote = null,
                    linkedConstellations = null
                )
            ),
            constellations = null,
            dictionary = null
        )

        val (talents, constellations) = mapTalentsAndConstellations(2, "en", detailDto)

        assertEquals(1, talents.size)
        assertEquals(0, constellations.size)
    }

    @Test
    fun testMapTalentsAndConstellationsWithEmptyConstellations() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000008",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = mapOf(
                "1" to YattaTalentDto(
                    name = "Normal Attack",
                    description = "Test",
                    icon = "Skill_A_01",
                    type = 0,
                    promote = null,
                    linkedConstellations = null
                )
            ),
            constellations = emptyMap(),
            dictionary = null
        )

        val (talents, constellations) = mapTalentsAndConstellations(3, "en", detailDto)

        assertEquals(1, talents.size)
        assertEquals(0, constellations.size)
    }

    @Test
    fun testMapTalentsAndConstellationsPreservesOrderIndex() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000009",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = mapOf(
                "1" to YattaTalentDto(
                    name = "Talent 1",
                    description = "Test",
                    icon = "Skill_A_01",
                    type = 0,
                    promote = null,
                    linkedConstellations = null
                ),
                "2" to YattaTalentDto(
                    name = "Talent 2",
                    description = "Test",
                    icon = "Skill_S_01",
                    type = 1,
                    promote = null,
                    linkedConstellations = null
                ),
                "3" to YattaTalentDto(
                    name = "Talent 3",
                    description = "Test",
                    icon = "Skill_E_01",
                    type = 2,
                    promote = null,
                    linkedConstellations = null
                )
            ),
            constellations = null,
            dictionary = null
        )

        val (talents, _) = mapTalentsAndConstellations(4, "en", detailDto)

        assertEquals(0, talents[0].orderIndex)
        assertEquals(1, talents[1].orderIndex)
        assertEquals(2, talents[2].orderIndex)
    }

    @Test
    fun testMapTalentsAndConstellationsPreservesConstellationOrder() {
        val detailDto = YattaAvatarDetailDto(
            id = "10000010",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(props = emptyList(), promote = null),
            talents = emptyMap(),
            constellations = mapOf(
                "1" to YattaConstellationDto(
                    name = "Const 1",
                    description = "Test",
                    icon = "UI_Talent_Const_01",
                    extraData = null
                ),
                "2" to YattaConstellationDto(
                    name = "Const 2",
                    description = "Test",
                    icon = "UI_Talent_Const_02",
                    extraData = null
                )
            ),
            dictionary = null
        )

        val (_, constellations) = mapTalentsAndConstellations(5, "en", detailDto)

        assertEquals(1, constellations[0].order)
        assertEquals(2, constellations[1].order)
    }

    @Test
    fun testCharacterEntityUpdateWithDetailsPreservesOtherFields() {
        val characterEntity = CharacterEntity(
            id = 10,
            name = "Original Name",
            element = Element.HYDRO,
            weaponType = WeaponType.BOW,
            rarity = 4,
            baseHpLvl1 = 100f,
            baseAtkLvl1 = 50f,
            baseDefLvl1 = 30f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "OLD_CURVE",
            iconUrl = "https://example.com/old.png",
            splashUrl = "https://example.com/old_splash.png"
        )

        val detailDto = YattaAvatarDetailDto(
            id = "10000011",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            upgrade = YattaUpgradeDto(
                props = listOf(
                    YattaPropDto("FIGHT_PROP_BASE_ATTACK", 25.0, "GROW_CURVE_ATTACK_S4")
                ),
                promote = null
            ),
            talents = emptyMap(),
            constellations = null,
            dictionary = null
        )

        val result = characterEntity.updateWithDetails(detailDto)

        assertEquals("Original Name", result.name)
        assertEquals(Element.HYDRO, result.element)
        assertEquals(WeaponType.BOW, result.weaponType)
        assertEquals(4, result.rarity)
        assertEquals("https://example.com/old.png", result.iconUrl)
    }
}
