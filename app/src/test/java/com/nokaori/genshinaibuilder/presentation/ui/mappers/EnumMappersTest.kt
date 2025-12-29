package com.nokaori.genshinaibuilder.presentation.ui.mappers

import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class EnumMappersTest {

    @Test
    fun testArtifactSlotFlowerOfLife() {
        val slot = ArtifactSlot.FLOWER_OF_LIFE
        assertNotNull(slot)
        assertEquals(ArtifactSlot.FLOWER_OF_LIFE, slot)
    }

    @Test
    fun testArtifactSlotPlumeOfDeath() {
        val slot = ArtifactSlot.PLUME_OF_DEATH
        assertNotNull(slot)
        assertEquals(ArtifactSlot.PLUME_OF_DEATH, slot)
    }

    @Test
    fun testArtifactSlotSandsOfEon() {
        val slot = ArtifactSlot.SANDS_OF_EON
        assertNotNull(slot)
        assertEquals(ArtifactSlot.SANDS_OF_EON, slot)
    }

    @Test
    fun testArtifactSlotGobletOfEonothem() {
        val slot = ArtifactSlot.GOBLET_OF_EONOTHEM
        assertNotNull(slot)
        assertEquals(ArtifactSlot.GOBLET_OF_EONOTHEM, slot)
    }

    @Test
    fun testArtifactSlotCircletOfLogos() {
        val slot = ArtifactSlot.CIRCLET_OF_LOGOS
        assertNotNull(slot)
        assertEquals(ArtifactSlot.CIRCLET_OF_LOGOS, slot)
    }

    @Test
    fun testWeaponTypeSword() {
        val type = WeaponType.SWORD
        assertNotNull(type)
        assertEquals(WeaponType.SWORD, type)
    }

    @Test
    fun testWeaponTypeClaymore() {
        val type = WeaponType.CLAYMORE
        assertNotNull(type)
        assertEquals(WeaponType.CLAYMORE, type)
    }

    @Test
    fun testWeaponTypePolearm() {
        val type = WeaponType.POLEARM
        assertNotNull(type)
        assertEquals(WeaponType.POLEARM, type)
    }

    @Test
    fun testWeaponTypeBow() {
        val type = WeaponType.BOW
        assertNotNull(type)
        assertEquals(WeaponType.BOW, type)
    }

    @Test
    fun testWeaponTypeCatalyst() {
        val type = WeaponType.CATALYST
        assertNotNull(type)
        assertEquals(WeaponType.CATALYST, type)
    }

    @Test
    fun testWeaponTypeUnknown() {
        val type = WeaponType.UNKNOWN
        assertNotNull(type)
        assertEquals(WeaponType.UNKNOWN, type)
    }

    @Test
    fun testStatTypeHp() {
        val stat = StatType.HP
        assertNotNull(stat)
        assertEquals(StatType.HP, stat)
        assertFalse(stat.isPercentage)
    }

    @Test
    fun testStatTypeAtk() {
        val stat = StatType.ATK
        assertNotNull(stat)
        assertEquals(StatType.ATK, stat)
        assertFalse(stat.isPercentage)
    }

    @Test
    fun testStatTypeDef() {
        val stat = StatType.DEF
        assertNotNull(stat)
        assertEquals(StatType.DEF, stat)
        assertFalse(stat.isPercentage)
    }

    @Test
    fun testStatTypeHpPercent() {
        val stat = StatType.HP_PERCENT
        assertNotNull(stat)
        assertEquals(StatType.HP_PERCENT, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeAtkPercent() {
        val stat = StatType.ATK_PERCENT
        assertNotNull(stat)
        assertEquals(StatType.ATK_PERCENT, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeDefPercent() {
        val stat = StatType.DEF_PERCENT
        assertNotNull(stat)
        assertEquals(StatType.DEF_PERCENT, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeCritRate() {
        val stat = StatType.CRIT_RATE
        assertNotNull(stat)
        assertEquals(StatType.CRIT_RATE, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeCritDmg() {
        val stat = StatType.CRIT_DMG
        assertNotNull(stat)
        assertEquals(StatType.CRIT_DMG, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeEnergyRecharge() {
        val stat = StatType.ENERGY_RECHARGE
        assertNotNull(stat)
        assertEquals(StatType.ENERGY_RECHARGE, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeElementalMastery() {
        val stat = StatType.ELEMENTAL_MASTERY
        assertNotNull(stat)
        assertEquals(StatType.ELEMENTAL_MASTERY, stat)
        assertFalse(stat.isPercentage)
    }

    @Test
    fun testStatTypeAneloDamageBonus() {
        val stat = StatType.ANEMO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.ANEMO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeGeoDamageBonus() {
        val stat = StatType.GEO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.GEO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeElectroDamageBonus() {
        val stat = StatType.ELECTRO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.ELECTRO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeCryoDamageBonus() {
        val stat = StatType.CRYO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.CRYO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeDendroDamageBonus() {
        val stat = StatType.DENDRO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.DENDRO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeHydroDamageBonus() {
        val stat = StatType.HYDRO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.HYDRO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypePhysicalDamageBonus() {
        val stat = StatType.PHYSICAL_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.PHYSICAL_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypePyroDamageBonus() {
        val stat = StatType.PYRO_DAMAGE_BONUS
        assertNotNull(stat)
        assertEquals(StatType.PYRO_DAMAGE_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeHealingBonus() {
        val stat = StatType.HEALING_BONUS
        assertNotNull(stat)
        assertEquals(StatType.HEALING_BONUS, stat)
        assertTrue(stat.isPercentage)
    }

    @Test
    fun testStatTypeUnknown() {
        val stat = StatType.UNKNOWN
        assertNotNull(stat)
        assertEquals(StatType.UNKNOWN, stat)
        assertFalse(stat.isPercentage)
    }

    @Test
    fun testAllArtifactSlotsExist() {
        val slots = listOf(
            ArtifactSlot.FLOWER_OF_LIFE,
            ArtifactSlot.PLUME_OF_DEATH,
            ArtifactSlot.SANDS_OF_EON,
            ArtifactSlot.GOBLET_OF_EONOTHEM,
            ArtifactSlot.CIRCLET_OF_LOGOS
        )

        assertEquals(5, slots.size)
        slots.forEach { assertNotNull(it) }
    }

    @Test
    fun testAllWeaponTypesExist() {
        val types = listOf(
            WeaponType.SWORD,
            WeaponType.CLAYMORE,
            WeaponType.POLEARM,
            WeaponType.BOW,
            WeaponType.CATALYST,
            WeaponType.UNKNOWN
        )

        assertEquals(6, types.size)
        types.forEach { assertNotNull(it) }
    }

    @Test
    fun testPercentageStatTypes() {
        val percentageStats = listOf(
            StatType.HP_PERCENT,
            StatType.ATK_PERCENT,
            StatType.DEF_PERCENT,
            StatType.CRIT_RATE,
            StatType.CRIT_DMG,
            StatType.ENERGY_RECHARGE,
            StatType.ANEMO_DAMAGE_BONUS,
            StatType.GEO_DAMAGE_BONUS,
            StatType.ELECTRO_DAMAGE_BONUS,
            StatType.CRYO_DAMAGE_BONUS,
            StatType.DENDRO_DAMAGE_BONUS,
            StatType.HYDRO_DAMAGE_BONUS,
            StatType.PHYSICAL_DAMAGE_BONUS,
            StatType.PYRO_DAMAGE_BONUS,
            StatType.HEALING_BONUS
        )

        percentageStats.forEach { stat ->
            assertTrue("$stat should be percentage", stat.isPercentage)
        }
    }

    @Test
    fun testNonPercentageStatTypes() {
        val nonPercentageStats = listOf(
            StatType.HP,
            StatType.ATK,
            StatType.DEF,
            StatType.ELEMENTAL_MASTERY,
            StatType.UNKNOWN
        )

        nonPercentageStats.forEach { stat ->
            assertFalse("$stat should not be percentage", stat.isPercentage)
        }
    }

    @Test
    fun testArtifactSlotOrdinal() {
        assertEquals(0, ArtifactSlot.FLOWER_OF_LIFE.ordinal)
        assertEquals(1, ArtifactSlot.PLUME_OF_DEATH.ordinal)
        assertEquals(2, ArtifactSlot.SANDS_OF_EON.ordinal)
        assertEquals(3, ArtifactSlot.GOBLET_OF_EONOTHEM.ordinal)
        assertEquals(4, ArtifactSlot.CIRCLET_OF_LOGOS.ordinal)
    }

    @Test
    fun testWeaponTypeComparison() {
        val sword = WeaponType.SWORD
        val claymore = WeaponType.CLAYMORE
        val polearm = WeaponType.POLEARM
        val bow = WeaponType.BOW
        val catalyst = WeaponType.CATALYST

        assertNotEquals(sword, claymore)
        assertNotEquals(claymore, polearm)
        assertNotEquals(polearm, bow)
        assertNotEquals(bow, catalyst)
    }

    @Test
    fun testStatTypeOrdinal() {
        val hp = StatType.HP
        val atk = StatType.ATK
        val def = StatType.DEF

        assertTrue(hp.ordinal < atk.ordinal)
        assertTrue(atk.ordinal < def.ordinal)
    }
}
