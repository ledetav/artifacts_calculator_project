package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponItemDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponUpgradeDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponPropDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponPromoteDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponAffixDto
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class YattaWeaponMapperTest {

    @Test
    fun testYattaWeaponItemDtoToEntity() {
        val dto = YattaWeaponItemDto(
            id = "11401",
            name = "Mistsplitter Reforged",
            rank = 5,
            type = "WEAPON_SWORD_ONE_HAND",
            specialProp = "FIGHT_PROP_CRITICAL_HURT",
            icon = "UI_EquipIcon_Sword_Mistsplitter",
            isWeaponSkin = false
        )

        val result = dto.toEntity()

        assertEquals(11401, result.id)
        assertEquals("Mistsplitter Reforged", result.name)
        assertEquals(5, result.rarity)
        assertEquals(WeaponType.SWORD, result.type)
        assertEquals(StatType.CRIT_DMG, result.subStatType)
        assertTrue(result.iconUrl.contains("UI_EquipIcon_Sword_Mistsplitter"))
    }

    @Test
    fun testYattaWeaponItemDtoToEntityWithNullValues() {
        val dto = YattaWeaponItemDto(
            id = "11402",
            name = null,
            rank = null,
            type = null,
            specialProp = null,
            icon = null,
            isWeaponSkin = null
        )

        val result = dto.toEntity()

        assertNotNull(result.name)
        assertEquals(1, result.rarity)
        assertEquals(WeaponType.UNKNOWN, result.type)
    }

    @Test
    fun testYattaWeaponItemDtoToEntityWithStringId() {
        val dto = YattaWeaponItemDto(
            id = "invalid_id",
            name = "Test Weapon",
            rank = 3,
            type = "WEAPON_CLAYMORE",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Claymore_Test",
            isWeaponSkin = false
        )

        val result = dto.toEntity()

        assertNotNull(result.id)
        assertEquals("Test Weapon", result.name)
    }

    @Test
    fun testWeaponEntityUpdateWithDetails() {
        val weaponEntity = WeaponEntity(
            id = 11403,
            name = "Aqua Simulacra",
            type = WeaponType.BOW,
            rarity = 5,
            baseAtkLvl1 = 0f,
            atkCurveId = "",
            subStatType = null,
            subStatBaseValue = null,
            subStatCurveId = null,
            iconUrl = "https://example.com/aqua.png"
        )

        val detailDto = YattaWeaponDetailDto(
            id = "11403",
            rank = 5,
            name = "Aqua Simulacra",
            specialProp = "FIGHT_PROP_CRITICAL_HURT",
            icon = "UI_EquipIcon_Bow_Aqua",
            description = "Test",
            affix = null,
            upgrade = YattaWeaponUpgradeDto(
                props = listOf(
                    YattaWeaponPropDto(
                        propType = "FIGHT_PROP_BASE_ATTACK",
                        initValue = 48.0,
                        curveId = "GROW_CURVE_ATTACK_S4"
                    ),
                    YattaWeaponPropDto(
                        propType = "FIGHT_PROP_CRITICAL_HURT",
                        initValue = 14.4,
                        curveId = "GROW_CURVE_CRITICAL_S4"
                    )
                ),
                promote = null
            )
        )

        val result = weaponEntity.updateWithDetails(detailDto)

        assertEquals(48f, result.baseAtkLvl1, 0.01f)
        assertEquals("GROW_CURVE_ATTACK_S4", result.atkCurveId)
        assertEquals(StatType.CRIT_DMG, result.subStatType)
        assertEquals(14.4f, result.subStatBaseValue ?: 0f, 0.01f)
    }

    @Test
    fun testWeaponEntityUpdateWithDetailsWithoutUpgrade() {
        val weaponEntity = WeaponEntity(
            id = 11404,
            name = "Test Weapon",
            type = WeaponType.SWORD,
            rarity = 3,
            baseAtkLvl1 = 0f,
            atkCurveId = "",
            subStatType = null,
            subStatBaseValue = null,
            subStatCurveId = null,
            iconUrl = "https://example.com/test.png"
        )

        val detailDto = YattaWeaponDetailDto(
            id = "11404",
            rank = 3,
            name = "Test Weapon",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Sword_Test",
            description = "Test",
            affix = null,
            upgrade = null
        )

        val result = weaponEntity.updateWithDetails(detailDto)

        assertEquals(weaponEntity, result)
    }

    @Test
    fun testMapWeaponRefinementsWithValidData() {
        val detailDto = YattaWeaponDetailDto(
            id = "11405",
            rank = 5,
            name = "Primordial Jade Cutter",
            specialProp = "FIGHT_PROP_CRITICAL_RATE",
            icon = "UI_EquipIcon_Sword_Jade",
            description = "Test",
            affix = mapOf(
                "1" to YattaWeaponAffixDto(
                    name = "Protector's Virtue",
                    upgrade = mapOf(
                        "0" to "Increases HP by 20%",
                        "1" to "Increases HP by 25%",
                        "2" to "Increases HP by 30%",
                        "3" to "Increases HP by 35%",
                        "4" to "Increases HP by 40%"
                    )
                )
            ),
            upgrade = null
        )

        val result = mapWeaponRefinements(11405, detailDto)

        assertNotNull(result)
        assertEquals("Protector's Virtue", result?.passiveName)
        assertEquals(5, result?.descriptions?.size)
    }

    @Test
    fun testMapWeaponRefinementsWithNullAffix() {
        val detailDto = YattaWeaponDetailDto(
            id = "11406",
            rank = 3,
            name = "Test Weapon",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Sword_Test",
            description = "Test",
            affix = null,
            upgrade = null
        )

        val result = mapWeaponRefinements(11406, detailDto)

        assertNull(result)
    }

    @Test
    fun testMapWeaponRefinementsWithEmptyAffix() {
        val detailDto = YattaWeaponDetailDto(
            id = "11407",
            rank = 3,
            name = "Test Weapon",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Sword_Test",
            description = "Test",
            affix = emptyMap(),
            upgrade = null
        )

        val result = mapWeaponRefinements(11407, detailDto)

        assertNull(result)
    }

    @Test
    fun testMapWeaponPromotionsWithValidData() {
        val detailDto = YattaWeaponDetailDto(
            id = "11408",
            rank = 5,
            name = "Wolf's Gravestone",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Claymore_Wolfs",
            description = "Test",
            affix = null,
            upgrade = YattaWeaponUpgradeDto(
                props = null,
                promote = listOf(
                    YattaWeaponPromoteDto(
                        level = 1,
                        addProps = mapOf(
                            "FIGHT_PROP_BASE_ATTACK" to 10.0,
                            "FIGHT_PROP_ATTACK_PERCENT" to 2.0
                        )
                    ),
                    YattaWeaponPromoteDto(
                        level = 2,
                        addProps = mapOf(
                            "FIGHT_PROP_BASE_ATTACK" to 20.0,
                            "FIGHT_PROP_ATTACK_PERCENT" to 4.0
                        )
                    )
                )
            )
        )

        val result = mapWeaponPromotions(11408, detailDto)

        assertEquals(2, result.size)
        assertEquals(1, result[0].ascensionLevel)
        assertEquals(10f, result[0].addAtk, 0.01f)
        assertEquals(2f, result[0].addSubStat ?: 0f, 0.01f)
    }

    @Test
    fun testMapWeaponPromotionsWithNullPromote() {
        val detailDto = YattaWeaponDetailDto(
            id = "11409",
            rank = 3,
            name = "Test Weapon",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Sword_Test",
            description = "Test",
            affix = null,
            upgrade = YattaWeaponUpgradeDto(props = null, promote = null)
        )

        val result = mapWeaponPromotions(11409, detailDto)

        assertEquals(0, result.size)
    }

    @Test
    fun testMapWeaponPromotionsWithoutUpgrade() {
        val detailDto = YattaWeaponDetailDto(
            id = "11410",
            rank = 3,
            name = "Test Weapon",
            specialProp = "FIGHT_PROP_ATTACK_PERCENT",
            icon = "UI_EquipIcon_Sword_Test",
            description = "Test",
            affix = null,
            upgrade = null
        )

        val result = mapWeaponPromotions(11410, detailDto)

        assertEquals(0, result.size)
    }

    @Test
    fun testYattaWeaponItemDtoToEntityWithDifferentWeaponTypes() {
        val weaponTypes = listOf(
            "WEAPON_SWORD_ONE_HAND" to WeaponType.SWORD,
            "WEAPON_CLAYMORE" to WeaponType.CLAYMORE,
            "WEAPON_POLE" to WeaponType.POLEARM,
            "WEAPON_BOW" to WeaponType.BOW,
            "WEAPON_CATALYST" to WeaponType.CATALYST
        )

        weaponTypes.forEach { (weaponStr, expectedType) ->
            val dto = YattaWeaponItemDto(
                id = "11411",
                name = "Test",
                rank = 5,
                type = weaponStr,
                specialProp = "FIGHT_PROP_ATTACK_PERCENT",
                icon = "UI_EquipIcon_Test",
                isWeaponSkin = false
            )

            val result = dto.toEntity()
            assertEquals(expectedType, result.type)
        }
    }
}
