package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

/**
 * Парсинг строки Yatta (API) в StatType (Domain).
 */
fun parseYattaStatType(raw: String?): StatType {
    if (raw == null) return StatType.UNKNOWN
    
    return when (raw) {
        "FIGHT_PROP_BASE_HP" -> StatType.HP
        "FIGHT_PROP_BASE_ATTACK" -> StatType.ATK
        "FIGHT_PROP_BASE_DEFENSE" -> StatType.DEF
        
        "FIGHT_PROP_HP" -> StatType.HP
        "FIGHT_PROP_ATTACK" -> StatType.ATK
        "FIGHT_PROP_DEFENSE" -> StatType.DEF
        
        "FIGHT_PROP_HP_PERCENT" -> StatType.HP_PERCENT
        "FIGHT_PROP_ATTACK_PERCENT" -> StatType.ATK_PERCENT
        "FIGHT_PROP_DEFENSE_PERCENT" -> StatType.DEF_PERCENT
        
        "FIGHT_PROP_CRITICAL" -> StatType.CRIT_RATE
        "FIGHT_PROP_CRITICAL_HURT" -> StatType.CRIT_DMG
        "FIGHT_PROP_CHARGE_EFFICIENCY" -> StatType.ENERGY_RECHARGE
        "FIGHT_PROP_ELEMENT_MASTERY" -> StatType.ELEMENTAL_MASTERY
        "FIGHT_PROP_HEAL_ADD" -> StatType.HEALING_BONUS
        
        "FIGHT_PROP_PHYSICAL_ADD_HURT" -> StatType.PHYSICAL_DAMAGE_BONUS
        "FIGHT_PROP_FIRE_ADD_HURT" -> StatType.PYRO_DAMAGE_BONUS
        "FIGHT_PROP_WATER_ADD_HURT" -> StatType.HYDRO_DAMAGE_BONUS
        "FIGHT_PROP_GRASS_ADD_HURT" -> StatType.DENDRO_DAMAGE_BONUS
        "FIGHT_PROP_ELEC_ADD_HURT" -> StatType.ELECTRO_DAMAGE_BONUS
        "FIGHT_PROP_WIND_ADD_HURT" -> StatType.ANEMO_DAMAGE_BONUS
        "FIGHT_PROP_ICE_ADD_HURT" -> StatType.CRYO_DAMAGE_BONUS
        "FIGHT_PROP_ROCK_ADD_HURT" -> StatType.GEO_DAMAGE_BONUS
        
        "NONE" -> StatType.UNKNOWN
        else -> StatType.UNKNOWN
    }
}

/**
 * Парсинг стихии.
 */
fun parseYattaElement(raw: String?): Element {
    if (raw == null) return Element.UNKNOWN

    return when (raw) {
        "Fire" -> Element.PYRO
        "Water" -> Element.HYDRO
        "Wind" -> Element.ANEMO
        "Electric" -> Element.ELECTRO
        "Grass" -> Element.DENDRO
        "Ice" -> Element.CRYO
        "Rock" -> Element.GEO
        else -> Element.UNKNOWN
    }
}

/**
 * Парсинг типа оружия.
 */
fun parseYattaWeaponType(raw: String?): WeaponType {
    if (raw == null) return WeaponType.UNKNOWN

    return when (raw) {
        "WEAPON_SWORD_ONE_HAND" -> WeaponType.SWORD
        "WEAPON_CLAYMORE" -> WeaponType.CLAYMORE
        "WEAPON_POLE" -> WeaponType.POLEARM
        "WEAPON_BOW" -> WeaponType.BOW
        "WEAPON_CATALYST" -> WeaponType.CATALYST
        "Sword" -> WeaponType.SWORD
        "Claymore" -> WeaponType.CLAYMORE
        "Pole" -> WeaponType.POLEARM
        "Polearm" -> WeaponType.POLEARM
        "Bow" -> WeaponType.BOW
        "Catalyst" -> WeaponType.CATALYST
        "None" -> WeaponType.UNKNOWN
        else -> WeaponType.UNKNOWN
    }
}