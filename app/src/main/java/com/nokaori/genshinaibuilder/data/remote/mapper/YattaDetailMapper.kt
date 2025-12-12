package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailDto
import com.nokaori.genshinaibuilder.domain.model.StatType

// Функция расширения: берет старую Entity и обновляет поля из DTO
fun CharacterEntity.updateWithDetails(dto: YattaAvatarDetailDto): CharacterEntity {
    
    // Ищем статы в списке props
    val hpProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_HP" }
    val atkProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_ATTACK" }
    val defProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_DEFENSE" }

    // Определяем Curve ID (обычно берем от Атаки или ХП, они часто имеют один суффикс S5/S4)
    val mainCurveId = atkProp?.curveId ?: "GROW_CURVE_ATTACK_S4"

    return this.copy(
        baseHpLvl1 = hpProp?.initValue?.toFloat() ?: 0f,
        baseAtkLvl1 = atkProp?.initValue?.toFloat() ?: 0f,
        baseDefLvl1 = defProp?.initValue?.toFloat() ?: 0f,
        
        ascensionStatType = mapYattaStatType(dto.specialProp),
        
        curveId = mainCurveId
    )
}

// Маппер игровых названий статов в наш Enum
private fun mapYattaStatType(raw: String): StatType {
    return when (raw) {
        "FIGHT_PROP_CRITICAL_HURT" -> StatType.CRIT_DMG
        "FIGHT_PROP_CRITICAL" -> StatType.CRIT_RATE
        "FIGHT_PROP_CHARGE_EFFICIENCY" -> StatType.ENERGY_RECHARGE
        "FIGHT_PROP_ELEMENT_MASTERY" -> StatType.ELEMENTAL_MASTERY
        "FIGHT_PROP_HP_PERCENT" -> StatType.HP_PERCENT
        "FIGHT_PROP_ATTACK_PERCENT" -> StatType.ATK_PERCENT
        "FIGHT_PROP_DEFENSE_PERCENT" -> StatType.DEF_PERCENT
        "FIGHT_PROP_HEAL_ADD" -> StatType.HEALING_BONUS
        "FIGHT_PROP_PHYSICAL_ADD_HURT" -> StatType.PHYSICAL_DAMAGE_BONUS
        "FIGHT_PROP_FIRE_ADD_HURT" -> StatType.PYRO_DAMAGE_BONUS
        "FIGHT_PROP_WATER_ADD_HURT" -> StatType.HYDRO_DAMAGE_BONUS
        "FIGHT_PROP_GRASS_ADD_HURT" -> StatType.DENDRO_DAMAGE_BONUS
        "FIGHT_PROP_ELEC_ADD_HURT" -> StatType.ELECTRO_DAMAGE_BONUS
        "FIGHT_PROP_WIND_ADD_HURT" -> StatType.ANEMO_DAMAGE_BONUS
        "FIGHT_PROP_ICE_ADD_HURT" -> StatType.CRYO_DAMAGE_BONUS
        "FIGHT_PROP_ROCK_ADD_HURT" -> StatType.GEO_DAMAGE_BONUS
        else -> StatType.ATK_PERCENT // Фолбэк
    }
}