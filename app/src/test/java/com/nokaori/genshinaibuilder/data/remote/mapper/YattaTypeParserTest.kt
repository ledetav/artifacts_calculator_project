package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class YattaTypeParserTest {

    @Test
    fun testParseYattaStatTypeHp() {
        assertEquals(StatType.HP, parseYattaStatType("FIGHT_PROP_BASE_HP"))
        assertEquals(StatType.HP, parseYattaStatType("FIGHT_PROP_HP"))
    }

    @Test
    fun testParseYattaStatTypeAtk() {
        assertEquals(StatType.ATK, parseYattaStatType("FIGHT_PROP_BASE_ATTACK"))
        assertEquals(StatType.ATK, parseYattaStatType("FIGHT_PROP_ATTACK"))
    }

    @Test
    fun testParseYattaStatTypeDef() {
        assertEquals(StatType.DEF, parseYattaStatType("FIGHT_PROP_BASE_DEFENSE"))
        assertEquals(StatType.DEF, parseYattaStatType("FIGHT_PROP_DEFENSE"))
    }

    @Test
    fun testParseYattaStatTypePercentages() {
        assertEquals(StatType.HP_PERCENT, parseYattaStatType("FIGHT_PROP_HP_PERCENT"))
        assertEquals(StatType.ATK_PERCENT, parseYattaStatType("FIGHT_PROP_ATTACK_PERCENT"))
        assertEquals(StatType.DEF_PERCENT, parseYattaStatType("FIGHT_PROP_DEFENSE_PERCENT"))
    }

    @Test
    fun testParseYattaStatTypeCritical() {
        assertEquals(StatType.CRIT_RATE, parseYattaStatType("FIGHT_PROP_CRITICAL"))
        assertEquals(StatType.CRIT_DMG, parseYattaStatType("FIGHT_PROP_CRITICAL_HURT"))
    }

    @Test
    fun testParseYattaStatTypeEnergyRecharge() {
        assertEquals(StatType.ENERGY_RECHARGE, parseYattaStatType("FIGHT_PROP_CHARGE_EFFICIENCY"))
    }

    @Test
    fun testParseYattaStatTypeElementalMastery() {
        assertEquals(StatType.ELEMENTAL_MASTERY, parseYattaStatType("FIGHT_PROP_ELEMENT_MASTERY"))
    }

    @Test
    fun testParseYattaStatTypeHealingBonus() {
        assertEquals(StatType.HEALING_BONUS, parseYattaStatType("FIGHT_PROP_HEAL_ADD"))
    }

    @Test
    fun testParseYattaStatTypeElementalDamageBonus() {
        assertEquals(StatType.PHYSICAL_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_PHYSICAL_ADD_HURT"))
        assertEquals(StatType.PYRO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_FIRE_ADD_HURT"))
        assertEquals(StatType.HYDRO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_WATER_ADD_HURT"))
        assertEquals(StatType.DENDRO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_GRASS_ADD_HURT"))
        assertEquals(StatType.ELECTRO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_ELEC_ADD_HURT"))
        assertEquals(StatType.ANEMO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_WIND_ADD_HURT"))
        assertEquals(StatType.CRYO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_ICE_ADD_HURT"))
        assertEquals(StatType.GEO_DAMAGE_BONUS, parseYattaStatType("FIGHT_PROP_ROCK_ADD_HURT"))
    }

    @Test
    fun testParseYattaStatTypeNone() {
        assertEquals(StatType.UNKNOWN, parseYattaStatType("NONE"))
    }

    @Test
    fun testParseYattaStatTypeUnknown() {
        assertEquals(StatType.UNKNOWN, parseYattaStatType("UNKNOWN_STAT"))
        assertEquals(StatType.UNKNOWN, parseYattaStatType(""))
    }

    @Test
    fun testParseYattaStatTypeNull() {
        assertEquals(StatType.UNKNOWN, parseYattaStatType(null))
    }

    @Test
    fun testParseYattaElementPyro() {
        assertEquals(Element.PYRO, parseYattaElement("Fire"))
    }

    @Test
    fun testParseYattaElementHydro() {
        assertEquals(Element.HYDRO, parseYattaElement("Water"))
    }

    @Test
    fun testParseYattaElementAnemo() {
        assertEquals(Element.ANEMO, parseYattaElement("Wind"))
    }

    @Test
    fun testParseYattaElementElectro() {
        assertEquals(Element.ELECTRO, parseYattaElement("Electric"))
    }

    @Test
    fun testParseYattaElementDendro() {
        assertEquals(Element.DENDRO, parseYattaElement("Grass"))
    }

    @Test
    fun testParseYattaElementCryo() {
        assertEquals(Element.CRYO, parseYattaElement("Ice"))
    }

    @Test
    fun testParseYattaElementGeo() {
        assertEquals(Element.GEO, parseYattaElement("Rock"))
    }

    @Test
    fun testParseYattaElementUnknown() {
        assertEquals(Element.UNKNOWN, parseYattaElement("Unknown"))
        assertEquals(Element.UNKNOWN, parseYattaElement(""))
    }

    @Test
    fun testParseYattaElementNull() {
        assertEquals(Element.UNKNOWN, parseYattaElement(null))
    }

    @Test
    fun testParseYattaWeaponTypeSword() {
        assertEquals(WeaponType.SWORD, parseYattaWeaponType("WEAPON_SWORD_ONE_HAND"))
        assertEquals(WeaponType.SWORD, parseYattaWeaponType("Sword"))
    }

    @Test
    fun testParseYattaWeaponTypeClaymore() {
        assertEquals(WeaponType.CLAYMORE, parseYattaWeaponType("WEAPON_CLAYMORE"))
        assertEquals(WeaponType.CLAYMORE, parseYattaWeaponType("Claymore"))
    }

    @Test
    fun testParseYattaWeaponTypePolearm() {
        assertEquals(WeaponType.POLEARM, parseYattaWeaponType("WEAPON_POLE"))
        assertEquals(WeaponType.POLEARM, parseYattaWeaponType("Pole"))
        assertEquals(WeaponType.POLEARM, parseYattaWeaponType("Polearm"))
    }

    @Test
    fun testParseYattaWeaponTypeBow() {
        assertEquals(WeaponType.BOW, parseYattaWeaponType("WEAPON_BOW"))
        assertEquals(WeaponType.BOW, parseYattaWeaponType("Bow"))
    }

    @Test
    fun testParseYattaWeaponTypeCatalyst() {
        assertEquals(WeaponType.CATALYST, parseYattaWeaponType("WEAPON_CATALYST"))
        assertEquals(WeaponType.CATALYST, parseYattaWeaponType("Catalyst"))
    }

    @Test
    fun testParseYattaWeaponTypeNone() {
        assertEquals(WeaponType.UNKNOWN, parseYattaWeaponType("None"))
    }

    @Test
    fun testParseYattaWeaponTypeUnknown() {
        assertEquals(WeaponType.UNKNOWN, parseYattaWeaponType("Unknown"))
        assertEquals(WeaponType.UNKNOWN, parseYattaWeaponType(""))
    }

    @Test
    fun testParseYattaWeaponTypeNull() {
        assertEquals(WeaponType.UNKNOWN, parseYattaWeaponType(null))
    }

    @Test
    fun testParseYattaArtifactSlotFlower() {
        assertEquals(ArtifactSlot.FLOWER_OF_LIFE, parseYattaArtifactSlot("EQUIP_BRACER"))
    }

    @Test
    fun testParseYattaArtifactSlotPlume() {
        assertEquals(ArtifactSlot.PLUME_OF_DEATH, parseYattaArtifactSlot("EQUIP_NECKLACE"))
    }

    @Test
    fun testParseYattaArtifactSlotSands() {
        assertEquals(ArtifactSlot.SANDS_OF_EON, parseYattaArtifactSlot("EQUIP_SHOES"))
    }

    @Test
    fun testParseYattaArtifactSlotGoblet() {
        assertEquals(ArtifactSlot.GOBLET_OF_EONOTHEM, parseYattaArtifactSlot("EQUIP_RING"))
    }

    @Test
    fun testParseYattaArtifactSlotCirclet() {
        assertEquals(ArtifactSlot.CIRCLET_OF_LOGOS, parseYattaArtifactSlot("EQUIP_DRESS"))
    }

    @Test
    fun testParseYattaArtifactSlotUnknown() {
        assertNull(parseYattaArtifactSlot("UNKNOWN_SLOT"))
        assertNull(parseYattaArtifactSlot(""))
    }

    @Test
    fun testParseYattaArtifactSlotNull() {
        assertNull(parseYattaArtifactSlot(""))
    }

    @Test
    fun testParseYattaStatTypeAllElementalDamages() {
        val elementalDamages = listOf(
            "FIGHT_PROP_PHYSICAL_ADD_HURT" to StatType.PHYSICAL_DAMAGE_BONUS,
            "FIGHT_PROP_FIRE_ADD_HURT" to StatType.PYRO_DAMAGE_BONUS,
            "FIGHT_PROP_WATER_ADD_HURT" to StatType.HYDRO_DAMAGE_BONUS,
            "FIGHT_PROP_GRASS_ADD_HURT" to StatType.DENDRO_DAMAGE_BONUS,
            "FIGHT_PROP_ELEC_ADD_HURT" to StatType.ELECTRO_DAMAGE_BONUS,
            "FIGHT_PROP_WIND_ADD_HURT" to StatType.ANEMO_DAMAGE_BONUS,
            "FIGHT_PROP_ICE_ADD_HURT" to StatType.CRYO_DAMAGE_BONUS,
            "FIGHT_PROP_ROCK_ADD_HURT" to StatType.GEO_DAMAGE_BONUS
        )

        elementalDamages.forEach { (input, expected) ->
            assertEquals(expected, parseYattaStatType(input))
        }
    }

    @Test
    fun testParseYattaElementAllElements() {
        val elements = listOf(
            "Fire" to Element.PYRO,
            "Water" to Element.HYDRO,
            "Wind" to Element.ANEMO,
            "Electric" to Element.ELECTRO,
            "Grass" to Element.DENDRO,
            "Ice" to Element.CRYO,
            "Rock" to Element.GEO
        )

        elements.forEach { (input, expected) ->
            assertEquals(expected, parseYattaElement(input))
        }
    }

    @Test
    fun testParseYattaWeaponTypeAllTypes() {
        val weaponTypes = listOf(
            "WEAPON_SWORD_ONE_HAND" to WeaponType.SWORD,
            "WEAPON_CLAYMORE" to WeaponType.CLAYMORE,
            "WEAPON_POLE" to WeaponType.POLEARM,
            "WEAPON_BOW" to WeaponType.BOW,
            "WEAPON_CATALYST" to WeaponType.CATALYST
        )

        weaponTypes.forEach { (input, expected) ->
            assertEquals(expected, parseYattaWeaponType(input))
        }
    }

    @Test
    fun testParseYattaArtifactSlotAllSlots() {
        val slots = listOf(
            "EQUIP_BRACER" to ArtifactSlot.FLOWER_OF_LIFE,
            "EQUIP_NECKLACE" to ArtifactSlot.PLUME_OF_DEATH,
            "EQUIP_SHOES" to ArtifactSlot.SANDS_OF_EON,
            "EQUIP_RING" to ArtifactSlot.GOBLET_OF_EONOTHEM,
            "EQUIP_DRESS" to ArtifactSlot.CIRCLET_OF_LOGOS
        )

        slots.forEach { (input, expected) ->
            assertEquals(expected, parseYattaArtifactSlot(input))
        }
    }
}
