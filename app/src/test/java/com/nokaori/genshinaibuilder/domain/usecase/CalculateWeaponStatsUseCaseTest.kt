package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CalculateWeaponStatsUseCaseTest {
    private lateinit var useCase: CalculateWeaponStatsUseCase

    @Before
    fun setup() {
        useCase = CalculateWeaponStatsUseCase()
    }

    @Test
    fun invoke_withWeaponAndLevel_calculatesBaseAttack() {
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = null,
            iconUrl = ""
        )
        val result = useCase(weapon, 1, 0)
        assertEquals(55, result.baseAttack)
    }

    @Test
    fun invoke_withHigherLevel_increasesBaseAttack() {
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = null,
            iconUrl = ""
        )
        val result = useCase(weapon, 90, 0)
        assertEquals(500, result.baseAttack)
    }

    @Test
    fun invoke_withMainStat_calculatesMainStatValue() {
        val mainStat = Stat(
            type = StatType.ATK_PERCENT,
            value = StatValue.DoubleValue(0.5)
        )
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = mainStat,
            iconUrl = ""
        )
        val result = useCase(weapon, 90, 0)
        assertNotNull(result.mainStat)
        assertEquals(StatType.ATK_PERCENT, result.mainStat?.type)
    }

    @Test
    fun invoke_withUserWeapon_calculatesStats() {
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = null,
            iconUrl = ""
        )
        val userWeapon = UserWeapon(
            id = 1,
            weapon = weapon,
            level = 80,
            ascension = 6,
            refinement = 1
        )
        val result = useCase(userWeapon)
        assertEquals(450, result.baseAttack)
    }

    @Test
    fun invoke_withoutMainStat_mainStatIsNull() {
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = null,
            iconUrl = ""
        )
        val result = useCase(weapon, 1, 0)
        assertEquals(null, result.mainStat)
    }

    @Test
    fun invoke_withIntMainStat_calculatesIntValue() {
        val mainStat = Stat(
            type = StatType.ELEMENTAL_MASTERY,
            value = StatValue.IntValue(100)
        )
        val weapon = Weapon(
            id = 1,
            name = "Test Sword",
            type = WeaponType.SWORD,
            rarity = Rarity.FIVE_STARS,
            baseAttackLvl1 = 50,
            scalingCurveId = "test",
            mainStat = mainStat,
            iconUrl = ""
        )
        val result = useCase(weapon, 90, 0)
        assertNotNull(result.mainStat)
        assertEquals(StatType.ELEMENTAL_MASTERY, result.mainStat?.type)
    }
}
