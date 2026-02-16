package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterWeaponsUseCaseTest {
    private lateinit var useCase: FilterWeaponsUseCase
    private lateinit var testWeapons: List<UserWeapon>

    @Before
    fun setup() {
        useCase = FilterWeaponsUseCase()
        testWeapons = listOf(
            UserWeapon(
                id = 1,
                weapon = Weapon(
                    id = 1,
                    name = "Primordial Jade Cutter",
                    type = WeaponType.SWORD,
                    rarity = Rarity.FIVE_STARS,
                    baseAttackLvl1 = 50,
                    scalingCurveId = "test",
                    mainStat = Stat(StatType.CRIT_RATE, StatValue.DoubleValue(0.192)),
                    iconUrl = ""
                ),
                level = 90,
                ascension = 6,
                refinement = 1
            ),
            UserWeapon(
                id = 2,
                weapon = Weapon(
                    id = 2,
                    name = "Favonius Sword",
                    type = WeaponType.SWORD,
                    rarity = Rarity.FOUR_STARS,
                    baseAttackLvl1 = 40,
                    scalingCurveId = "test",
                    mainStat = Stat(StatType.ENERGY_RECHARGE, StatValue.DoubleValue(0.306)),
                    iconUrl = ""
                ),
                level = 80,
                ascension = 5,
                refinement = 3
            ),
            UserWeapon(
                id = 3,
                weapon = Weapon(
                    id = 3,
                    name = "Skyward Harp",
                    type = WeaponType.BOW,
                    rarity = Rarity.FIVE_STARS,
                    baseAttackLvl1 = 48,
                    scalingCurveId = "test",
                    mainStat = Stat(StatType.CRIT_DMG, StatValue.DoubleValue(0.384)),
                    iconUrl = ""
                ),
                level = 90,
                ascension = 6,
                refinement = 1
            )
        )
    }

    @Test
    fun invoke_withEmptySearchQuery_returnsAllWeapons() {
        val result = useCase(
            testWeapons,
            "",
            emptySet(),
            0f..90f,
            null
        )
        assertEquals(3, result.size)
    }

    @Test
    fun invoke_withSearchQuery_filtersWeapons() {
        val result = useCase(
            testWeapons,
            "Primordial",
            emptySet(),
            0f..90f,
            null
        )
        assertEquals(1, result.size)
    }

    @Test
    fun invoke_withSelectedWeaponTypes_filtersByType() {
        val result = useCase(
            testWeapons,
            "",
            setOf(WeaponType.BOW),
            0f..90f,
            null
        )
        assertEquals(1, result.size)
        assertEquals("Skyward Harp", result.first().weapon.name)
    }

    @Test
    fun invoke_withLevelRange_filtersByLevel() {
        val result = useCase(
            testWeapons,
            "",
            emptySet(),
            80f..80f,
            null
        )
        assertEquals(1, result.size)
        assertEquals("Favonius Sword", result.first().weapon.name)
    }

    @Test
    fun invoke_withSelectedMainStat_filtersByMainStat() {
        val result = useCase(
            testWeapons,
            "",
            emptySet(),
            0f..90f,
            StatType.CRIT_RATE
        )
        assertEquals(1, result.size)
        assertEquals("Primordial Jade Cutter", result.first().weapon.name)
    }

    @Test
    fun invoke_withMultipleFilters_appliesAllFilters() {
        val result = useCase(
            testWeapons,
            "Primordial",
            setOf(WeaponType.SWORD),
            90f..90f,
            StatType.CRIT_RATE
        )
        assertEquals(1, result.size)
        assertEquals("Primordial Jade Cutter", result.first().weapon.name)
    }

    @Test
    fun invoke_sortsByRarityDescendingThenByLevelDescendingThenByName() {
        val result = useCase(
            testWeapons,
            "",
            emptySet(),
            0f..90f,
            null
        )
        assertEquals("Primordial Jade Cutter", result[0].weapon.name)
        assertEquals("Skyward Harp", result[1].weapon.name)
        assertEquals("Favonius Sword", result[2].weapon.name)
    }

    @Test
    fun invoke_withNoMatches_returnsEmptyList() {
        val result = useCase(
            testWeapons,
            "NonExistent",
            emptySet(),
            0f..90f,
            null
        )
        assertEquals(0, result.size)
    }

    @Test
    fun invoke_caseInsensitiveSearch() {
        val result = useCase(
            testWeapons,
            "FAVONIUS",
            emptySet(),
            0f..90f,
            null
        )
        assertEquals(1, result.size)
        assertEquals("Favonius Sword", result.first().weapon.name)
    }
}
