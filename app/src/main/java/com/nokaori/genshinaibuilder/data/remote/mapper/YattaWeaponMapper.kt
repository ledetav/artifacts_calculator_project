package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponItemDto
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaWeaponItemDto.toEntity(): WeaponEntity {
    val safeId = this.id
    val safeName = this.name ?: "Unknown Weapon"
    val safeRank = this.rank ?: 1
    val safeType = this.type ?: "UNKNOWN"
    val safeSpecialProp = this.specialProp ?: "NONE"
    val safeIcon = this.icon ?: "UI_EquipIcon_Sword_Blunt"

    return WeaponEntity(
        id = safeId.toIntOrNull() ?: safeId.hashCode(),
        name = safeName,
        type = parseWeaponType(safeType),
        rarity = safeRank,
        baseAtkLvl1 = 0f,
        subStatType = parseYattaStatType(safeSpecialProp),
        subStatBaseValue = null,
        atkCurveId = "", 
        subStatCurveId = null,
        iconUrl = "$ASSETS_URL/$safeIcon.png"
    )
}

fun WeaponEntity.updateWithDetails(dto: YattaWeaponDetailDto): WeaponEntity {
    val upgrade = dto.upgrade ?: return this 

    val baseAtkProp = upgrade.props?.find { it.propType == "FIGHT_PROP_BASE_ATTACK" }
    val subStatProp = upgrade.props?.find { it.propType == dto.specialProp }

    val parsedSubStatType = parseYattaStatType(dto.specialProp ?: "NONE")
    val finalSubStatType = if (parsedSubStatType == StatType.UNKNOWN) null else parsedSubStatType

    return this.copy(
        baseAtkLvl1 = baseAtkProp?.initValue?.toFloat() ?: 0f,
        atkCurveId = baseAtkProp?.curveId ?: "",
        subStatType = finalSubStatType,
        subStatBaseValue = subStatProp?.initValue?.toFloat(), 
        subStatCurveId = subStatProp?.curveId 
    )
}

fun mapWeaponRefinements(weaponId: Int, dto: YattaWeaponDetailDto): WeaponRefinementEntity? {
    val affixMap = dto.affix ?: return null
    val firstAffix = affixMap.values.firstOrNull() ?: return null
    
    val passiveName = firstAffix.name ?: "Unknown Passive"

    val descriptions = (0..4).map { index ->
        firstAffix.upgrade?.get(index.toString()) ?: ""
    }

    return WeaponRefinementEntity(
        weaponId = weaponId,
        passiveName = passiveName,
        descriptions = descriptions.filter { it.isNotBlank() }.map { cleanDescription(it) }
    )
}

fun mapWeaponPromotions(weaponId: Int, dto: YattaWeaponDetailDto): List<WeaponPromotionEntity> {
    val promoteList = dto.upgrade?.promote ?: return emptyList()

    return promoteList.map { pDto ->
        val addProps = pDto.addProps ?: emptyMap()

        WeaponPromotionEntity(
            weaponId = weaponId,
            ascensionLevel = pDto.level ?: 0,
            addAtk = addProps["FIGHT_PROP_BASE_ATTACK"]?.toFloat() ?: 0f,
            addSubStat = addProps[dto.specialProp]?.toFloat() 
        )
    }
}

private fun parseWeaponType(yattaWeapon: String): WeaponType {
    return when (yattaWeapon) {
        "WEAPON_SWORD_ONE_HAND" -> WeaponType.SWORD
        "WEAPON_CLAYMORE" -> WeaponType.CLAYMORE
        "WEAPON_POLE" -> WeaponType.POLEARM
        "WEAPON_BOW" -> WeaponType.BOW
        "WEAPON_CATALYST" -> WeaponType.CATALYST
        else -> WeaponType.UNKNOWN
    }
}

// Этот метод уже был в YattaDetailMapper.kt, но нужен и здесь.
// Лучше вынести в отдельный файл (например, StatTypeMapper.kt).
// Пока дублируем для компиляции.
fun parseYattaStatType(raw: String): StatType {
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
        "NONE" -> StatType.UNKNOWN 
        else -> StatType.UNKNOWN 
    }
}

private fun cleanDescription(raw: String): String {
    return raw.replace(Regex("<[^>]*>"), "")
              .replace("\\n", "\n")
}