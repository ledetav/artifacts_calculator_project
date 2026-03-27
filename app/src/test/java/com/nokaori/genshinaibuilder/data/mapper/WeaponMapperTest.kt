package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class WeaponMapperTest {

    @Test
    fun testWeaponEntityToDomain() {
        val weaponEntity = WeaponEntity(
            id = 1,
            language = "en",
            name = "Mistsplitter Reforged",
            type = WeaponType.SWORD,
            rarity = 5,
            baseAtkLvl1 = 48f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.CRIT_DMG,
            subStatBaseValue = 9.6f,
            subStatCurveId = "GROW_CURVE_CRITICAL_S4",
            iconUrl = "https://example.com/mistsplitter.png"
        )

        val result = weaponEntity.toDomain()

        assertEquals(1, result.id)
        assertEquals("Mistsplitter Reforged", result.name)
        assertEquals(WeaponType.SWORD, result.type)
        assertEquals(Rarity.FIVE_STARS, result.rarity)
        assertEquals(48, result.baseAttackLvl1)
        assertEquals("GROW_CURVE_ATTACK_S4", result.scalingCurveId)
        assertNotNull(result.mainStat)
        assertEquals(StatType.CRIT_DMG, result.mainStat?.type)
        assertEquals(9.6, (result.mainStat?.value as StatValue.DoubleValue).value, 0.01)
    }

    @Test
    fun testWeaponEntityToDomainWithoutSubStat() {
        val weaponEntity = WeaponEntity(
            id = 2,
            language = "en",
            name = "Iron Sting",
            type = WeaponType.SWORD,
            rarity = 3,
            baseAtkLvl1 = 42f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = null,
            subStatBaseValue = null,
            subStatCurveId = null,
            iconUrl = "https://example.com/iron_sting.png"
        )

        val result = weaponEntity.toDomain()

        assertEquals(2, result.id)
        assertEquals("Iron Sting", result.name)
        assertEquals(Rarity.THREE_STARS, result.rarity)
        assertNull(result.mainStat)
    }

    @Test
    fun testWeaponEntityToDomainWithRefinement() {
        val weaponEntity = WeaponEntity(
            id = 3,
            language = "en",
            name = "Primordial Jade Cutter",
            type = WeaponType.SWORD,
            rarity = 5,
            baseAtkLvl1 = 44f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.CRIT_RATE,
            subStatBaseValue = 9.0f,
            subStatCurveId = "GROW_CURVE_CRITICAL_S4",
            iconUrl = "https://example.com/jade_cutter.png"
        )

        val refinementEntity = WeaponRefinementEntity(
            weaponId = 3,
            passiveName = "Protector's Virtue",
            descriptions = listOf(
                "Increases HP by 20%",
                "Increases HP by 25%",
                "Increases HP by 30%"
            )
        )

        val result = weaponEntity.toDomain(refinementEntity)

        assertEquals(3, result.id)
        assertNotNull(result.refinement)
        assertEquals("Protector's Virtue", result.refinement?.passiveName)
        assertEquals(3, result.refinement?.descriptions?.size)
    }

    @Test
    fun testUserWeaponCompleteToDomain() {
        val weaponEntity = WeaponEntity(
            id = 4,
            language = "en",
            name = "Aqua Simulacra",
            type = WeaponType.BOW,
            rarity = 5,
            baseAtkLvl1 = 48f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.CRIT_DMG,
            subStatBaseValue = 14.4f,
            subStatCurveId = "GROW_CURVE_CRITICAL_S4",
            iconUrl = "https://example.com/aqua_simulacra.png"
        )

        val userWeaponEntity = UserWeaponEntity(
            id = 1,
            weaponId = 4,
            level = 90,
            ascension = 6,
            refinement = 1,
            isLocked = true,
            equippedCharacterId = null
        )

        val userWeaponComplete = UserWeaponComplete(
            userWeapon = userWeaponEntity,
            weaponEntity = weaponEntity
        )

        val result = userWeaponComplete.toDomain()

        assertEquals(1, result.id)
        assertEquals("Aqua Simulacra", result.weapon.name)
        assertEquals(90, result.level)
        assertEquals(6, result.ascension)
        assertEquals(1, result.refinement)
        assertTrue(result.isLocked)
    }

    @Test
    fun testUserWeaponCompleteToDomainWithLowLevel() {
        val weaponEntity = WeaponEntity(
            id = 5,
            language = "en",
            name = "Favonius Sword",
            type = WeaponType.SWORD,
            rarity = 4,
            baseAtkLvl1 = 41f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.ENERGY_RECHARGE,
            subStatBaseValue = 13.3f,
            subStatCurveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/favonius.png"
        )

        val userWeaponEntity = UserWeaponEntity(
            id = 2,
            weaponId = 5,
            level = 20,
            ascension = 1,
            refinement = 3,
            isLocked = false,
            equippedCharacterId = null
        )

        val userWeaponComplete = UserWeaponComplete(
            userWeapon = userWeaponEntity,
            weaponEntity = weaponEntity
        )

        val result = userWeaponComplete.toDomain()

        assertEquals(2, result.id)
        assertEquals(20, result.level)
        assertEquals(1, result.ascension)
        assertEquals(3, result.refinement)
        assertFalse(result.isLocked)
    }

    @Test
    fun testWeaponEntityToDomainWithIntegerSubStat() {
        val weaponEntity = WeaponEntity(
            id = 6,
            language = "en",
            name = "Skyward Blade",
            type = WeaponType.SWORD,
            rarity = 5,
            baseAtkLvl1 = 46f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.ATK_PERCENT,
            subStatBaseValue = 12.0f,
            subStatCurveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/skyward_blade.png"
        )

        val result = weaponEntity.toDomain()

        assertNotNull(result.mainStat)
        assertEquals(StatType.ATK_PERCENT, result.mainStat?.type)
        assertEquals(12.0, (result.mainStat?.value as StatValue.DoubleValue).value, 0.01)
    }

    @Test
    fun testWeaponEntityToDomainPreservesAllData() {
        val weaponEntity = WeaponEntity(
            id = 7,
            language = "en",
            name = "Wolf's Gravestone",
            type = WeaponType.CLAYMORE,
            rarity = 5,
            baseAtkLvl1 = 46f,
            atkCurveId = "GROW_CURVE_ATTACK_S4",
            subStatType = StatType.ATK_PERCENT,
            subStatBaseValue = 10.8f,
            subStatCurveId = "GROW_CURVE_ATTACK_S4",
            iconUrl = "https://example.com/wolfs_gravestone.png"
        )

        val result = weaponEntity.toDomain()

        assertEquals(7, result.id)
        assertEquals("Wolf's Gravestone", result.name)
        assertEquals(WeaponType.CLAYMORE, result.type)
        assertEquals(Rarity.FIVE_STARS, result.rarity)
        assertEquals("https://example.com/wolfs_gravestone.png", result.iconUrl)
    }
}
